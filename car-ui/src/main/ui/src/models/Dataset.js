import { car } from "@/car.js";

function fsnode(n, path, dir, dataset) {
  this.name = n;
  this.title = n;
  this.path = path;
  this.expandedIcon = dir ? "folder-open" : "file-alt";
  this.collapsedIcon = dir ? "folder" : "file-alt";
  this.iconcolor = dir ? "skyblue" : "white";
  //this.txtcolor = "white";
  this.children = [];
  this.newChildNode = dir;
  this.expanded = function (path) {
    if (this.children.length === 0)
      return false;
    let $ = encodeURI;
    let expected = `/ui/datasets/${$(dataset.id)}/reports/default/${$(this.path)}`;
    console.log('bango', expected, path)
    return path.startsWith(expected)
  }
  this.getHrefParams = () => {
    return { dsetid: dataset.id, ds: dataset, filename: path, report: 'default' }
  }
  this.href = {
    name: "fileview",
  };
  this.addChild = (name, node) => {
    let filt = this.children.filter(x => x.name === name)
    if (filt.length != 0)
      return filt[0];
    this.children.push(node);
    return node;
  }
  if (dir) {
    this.getChildren = function () {
      return Promise.resolve(Object.values(this.children));
    };
  }
}

export default class Dataset {
  constructor(ds = {}) {
    this.loaded = (ds.loaded != undefined) ? ds.loaded : false;
    this.merge(ds);
  }

  loadDetails() {
    if (this.loadProm)
      return this.loadProm;

    return this.loadProm = car.getDataset(this.id).then((ds) => {
      try {
        this.merge(ds);
      } finally {
        this.loaded = true;
      }
    });
  }

  merge(ds) {

    this.id = ds.id
    if (!this.name)
      this.title = this.name = (this.owner && (car.currentUid() != ds.owner)) ? `${ds.owner} / ${ds.name}` : ds.name;
    this.owner = ds.owner
    this.description = ds.description || "";
    this.types = ds.types || [];
    this.files = ds.files || [];

    this.expanded = function (path) {
      let $ = encodeURI;
      return (path + "/").startsWith(`/ui/datasets/${$(this.id)}/`)
    }

    this.localDataset = ds.localDataset || false;
    this.localPath = ds.localPath || "<no local path>";

    this.expandedIcon = "folder-open";
    this.collapsedIcon = "folder";
    this.iconcolor = "primary";
    this.newChildNode = false;
    this.children = [];
    this.getHrefParams = () => {
      return { dataset: this, dsetid: this.id }
    }
    this.href = {
      name: "datasetdetail"
    };
    this.addChild = (name, node) => {
      let idx = this.children.findIndex(x => x.name === name);
      if (idx != -1) return this.children[idx];
      this.children.push(node);
      return node;
    }

    this.fileStatus = ds.fileStatus;

    this.getChildren = () => {
      if (!this.loaded) {
        return this.loadDetails()
      }
      return Promise.resolve();
    }
    this.mergeStatus(this.fileStatus);
  }

  toJSON() {
    return { ...this, href: undefined }
  }

  mergeStatus(fsm) {
    if (!fsm)
      return;
    this.children.splice(0, this.children.length);
    this.fileStatus = fsm;
    Object.keys(fsm).forEach((p) => {
      let node = this,
        npath = "",
        sep = p.includes("/") ? "/" : "\\",
        path = p;
      path.split(/\\|\//).forEach((pc) => {
        node = node.addChild(
          pc,
          new fsnode(pc, (npath += sep + pc).substring(1), !p.endsWith(pc), this)
        );
      });
      node.attr = fsm[p];
      node.attr.path = path;
    });
  }

  updateStatus() {
    return car.status(this.id).then((status) => {
      this.mergeStatus(status);
      return status;
    });
  }
};
