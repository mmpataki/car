import { Store } from "./Store.js";

export default class JsonDataStore extends Store {
  constructor(data) {
    super("json");
    this.json = data.json;
  }
  async getData() {
    return JSON.parse(this.json || "[]");
  }
  requiresUpdate() {
    return false;
  }
  toJSON() {
    return { ...this, data: undefined };
  }
}
