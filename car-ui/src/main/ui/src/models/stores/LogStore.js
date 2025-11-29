import { Store } from "./Store.js";
import { car } from "@/car.js";

export default class LogStore extends Store {
  constructor(sd) {
    super("logs");
    this.query = JSON.stringify(sd.query ? JSON.parse(sd.query) : { "*": "*" }, null, 4);
    this.mode = sd.mode || "facet"; // two modes, facet & logs
    this.pageSize = sd.pageSize || 20;
    this.sortFields = sd.sortFields || [];
    this.queriedFields = sd.queriedFields || [];
    this.facetFields = sd.facetFields || [];
    this.rangeFacets = sd.rangeFacets || [];
    this.statFields = sd.statFields || [];
    this.dsetid = sd.dsetid;
    this.cursor = "0";
    this.schemaGet = this.getStoreSchema()
  }
  requiresUpdate(cl) {
    for (let c of cl) {
      if (c in this.query) {
        return true;
      }
    }
    return false;
  }
  getStoreSchema() {
    if (this.schemaGet)
      return this.schemaGet
    return car.getStoreSchema(this.dsetid, 'logs').then((schema) => {
      console.log(schema)
      this.fields = schema.schema.map(x => x.name);
      if (this.facetFields.length == 0) {
        this.facetFields = this.fields.filter(x => !["_msg", "_line", "_version_", "id"].includes(x));
      }
      return schema;
    });
  }

  _getData(cursor, ctxt) {
    try {
      if (!this.query)
        return Promise.resolve([])

      let query = {}
      Object.entries(JSON.parse(this.query)).forEach(([k, v]) => {
        let e = k => car.evalTemplateStringWithCtxt(k, ctxt)
        query[e(k)] = e(v)
      })

      return this.schemaGet.then(() => {
        let q = {
          cursor,
          pageSize: this.mode == 'logs' ? this.pageSize : 0,
          query,
          sortFields: this.sortFields,
          fields: (this.allFields ? [] : this.queriedFields).concat(['id']),
          facet: true,
          facetFields: this.facetFields.length != 0 ? this.facetFields : this.fields.filter(x => !["_msg", "_line", "_version_", "id"].includes(x)),
          rangeFacets: this.rangeFacets,
          statFields: this.statFields,
          suggestView: this.suggestView
        }

        return this.doSearch(q).then(resp => {
          this.cursor = resp.cursor;
          this.facets = resp.facets;
          this.stats = resp.stats;
          this.suggestedView = resp.suggestedView
          let ret = (this.mode == 'facet')
            ? Object.entries(Object.values(Object.keys(resp.rangeFacets).length ? resp.rangeFacets : resp.facets)[0]).map(([k, v]) => ({ name: k, count: v }))
            : (
              (this.mode == 'stats')
                ? Object.entries(resp.stats).map(([k, v]) => ({key: k, ...v}))
                : resp.msgs
            );
          ret.props = { totalMessages: resp.totalHits, offset: cursor, fQueried: this.queriedFields, query: this.query, sortFields: JSON.stringify(this.sortFields) }
          return ret
        });
      })
    } catch (e) {
      console.error(e)
      return Promise.resolve([]);
    }
  }

  doSearch(q) {
    return car.searchLog(this.dsetid, q);
  }

  getData(ctxt, reset) {
    if (reset)
      this.cursor = undefined
    return this._getData(this.cursor, ctxt);
  }

}
