import { Store } from "./Store.js";
import { car } from "@/car.js";

export default class SqlStore extends Store {
  constructor(sd) {
    super("sql");
    this.sql = sd ? sd.sql : "select _rule, _line from some_table";
    this.dsetid = sd.dsetid;
  }
  requiresUpdate(cl) {
    // cl => changed props list
    let m = this.sql.matchAll(/\$\{(.*?)\}/g);
    for (let c = m.next(); !c.done; c = m.next()) {
      for (let i = 0; i < cl.length; i++) {
        if (c.value[1].includes(cl[i])) {
          return true;
        }
      }
    }
    return false;
  }
  getStoreSchema() {
    return car.getStoreSchema(this.dsetid, 'sql');
  }
  getData(ctxt) {
    try {
      if (!this.sql)
        return Promise.resolve([])
      let sql = car.evalTemplateStringWithCtxt(this.sql, ctxt);
      return car.sql(this.dsetid, sql).then(results => {
        let { data, fieldNames } = results;
        let unfilt = []
        for (let j = 0; j < data.length; j++) {
          let datum = data[j], obj = {};
          for (let i = 0; i < fieldNames.length; i++) {
            obj[fieldNames[i]] = datum[i]
          }
          unfilt.push(obj)
        }
        return unfilt
      });
    } catch (e) {
      return Promise.resolve([]);
    }
  }
}
