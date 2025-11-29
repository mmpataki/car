import createViz from "@/models/visualizations/VisFactory.js"

export class Dashboard {
  constructor(db = {}) {

    Object.entries(db).forEach(([k, v]) => {
      this[k] = v;
    })

    this.name = db.name || "Untitled";
    this.description = db.description || "";
    
    this.ctxt = db.ctxt || {};
    this.visualizations = [];

    let vis = db.visualizations || [];

    vis.forEach((v) => {
      let visualization = undefined;
      try {
        v.ctxt = this.ctxt;
        visualization = createViz(v.type, v);
        this.visualizations.push(visualization);
        if (visualization.init) visualization.init();
      } catch (e) {
        if (visualization)
          visualization.error = "" + e;
        console.log(e)
      }
    });
  }
  addVisualization(vis) {
    this.visualizations.push(vis);
  }
  setVariable(k, v) {
    this.ctxt[k] = v;
  }
}
