import JsonDataStore from "./JsonDataStore.js";
import SqlStore from "./SqlStore.js";
import LogStore from "./LogStore.js";

let storeMakers = {
  sql: (_) => new SqlStore(_),
  json: (_) => new JsonDataStore(_),
  logs: (_) => new LogStore(_)
};

export function storeFactory(type, arg1, arg2, arg3) {
  return storeMakers[type](arg1, arg2, arg3);
}
