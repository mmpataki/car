import Rule from "@/models/rules/Rule.js";
import LogTypeGroup from "@/models/rules/LogTypeGroup.js";
import LogType from "@/models/rules/LogType.js";
import Dataset from "@/models/Dataset.js";

const USER_KEY = "user",
  TOK_KEY = "tok";
let baseUrl = "", aSync = false;
let alertHandler = (x) => console.log(x)

export function _ajax(method, url, data, hdrs, cancelToken) {
  if (getCurrentUser()) {
    if (!hdrs) hdrs = {};
    hdrs["AuthInfo"] = `${getCurrentUser()}:${getCookie(TOK_KEY)}`;
  }
  var xhttp = new XMLHttpRequest();
  let prom = new Promise((resolve, reject) => {
    if (cancelToken) {
      cancelToken.cancel = function () {
        xhttp.abort();
        reject(new Error("Cancelled"));
      };
    }
    xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
        let json;
        try {
          json = JSON.parse(this.responseText);
        } catch (e) {
          console.debug(e);
        }
        resolve({
          response: this.responseText,
          json,
          headers: makeHMap(xhttp.getAllResponseHeaders()),
        });
      }
      if (this.readyState == 4 && this.status > 399) {
        console.log('rejecting it')
        reject({
          message: JSON.parse(this.responseText).message,
          code: this.status,
        });
        alertHandler({
          message: JSON.parse(this.responseText).message,
          code: this.status,
          type: 'error'
        })
        console.log('rejectedddddddddddddddddddddddddd')
      }
    };
    xhttp.onerror = function () {
      reject({
        message: JSON.parse(this.responseText).message,
        code: this.status,
      });
    };
    xhttp.open(method, url, aSync);
    hdrs &&
      Object.keys(hdrs).forEach((key) =>
        xhttp.setRequestHeader(key, hdrs[key])
      );
    xhttp.send(data);
  });
  prom.xhr = xhttp;
  return prom;
}

export function ajax(method, url, data, hdrs, cancelToken) {
  return _ajax(method, `${baseUrl}${url}`, data, hdrs, cancelToken);
}

function makeHMap(headers) {
  var arr = headers.trim().split(/[\r\n]+/);
  var headerMap = {};
  arr.forEach(function (line) {
    var parts = line.split(": ");
    var header = parts.shift();
    var value = parts.join(": ");
    headerMap[header] = value;
  });
  return headerMap;
}

export function get(url, token) {
  return ajax("GET", url, undefined, {}, token);
}

export function post(url, data, hdrs) {
  if (typeof data !== "string") {
    hdrs = { "Content-Type": "application/json", ...hdrs };
  }
  return ajax("POST", url, JSON.stringify(data), hdrs);
}

export function postFile(url, data, hdrs, updateProgress) {
  let prom = ajax("POST", url, data, hdrs);
  prom.xhr.onprogress = function () {
    console.log(this.arguments)
    updateProgress(this.arguments);
  }
  return prom;
}

export function delet(url, data) {
  return ajax("DELETE", url, data);
}

// WYSIWYG
export function _get(url, token) {
  return _ajax("GET", url, undefined, {}, token);
}

export function _post(url, data, hdrs) {
  return _ajax("POST", url, JSON.stringify(data), hdrs);
}

export function _postFile(url, data, hdrs) {
  return _ajax("POST", url, data, hdrs);
}

export function _delet(url) {
  return _ajax("DELETE", url);
}

export function ncors_get(url, data, hdrs) {
  return nocors("GET", url, data, hdrs);
}

export function ncors_post(url, data, hdrs) {
  return nocors("POST", url, data, hdrs);
}

export function ncors_delete(url, data, hdrs) {
  return nocors("DELETE", url, data, hdrs);
}

export function ncors_put(url, data, hdrs) {
  return nocors("PUT", url, data, hdrs);
}

export function nocors(method, url, data, headers) {
  return post(
    `/nocors`,
    { method: method, data: JSON.stringify(data), headers, url: url },
    { "Content-Type": "application/json" }
  );
}

export function setCookie(name, value, days) {
  var expires = "";
  if (days) {
    var date = new Date();
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
    expires = "; expires=" + date.toUTCString();
  }
  document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

function getCookie(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(";");
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == " ") c = c.substring(1, c.length);
    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
  }
  return null;
}

function eraseCookie(name) {
  document.cookie = name + "=; Max-Age=-99999999;";
}

function getCurrentUser() {
  return getCookie(USER_KEY);
}

/**
 * makes an HTML element fullscreen
 * requirement:
 *      `ele` shouldn't have any siblings
 */
export function makeFullScreen(ele) {
  let x = document.createElement("div");
  x.classList = "fullscreen-inode";
  document.body.appendChild(x);

  let cardParent = ele.parentNode,
    closeBtn = document.createElement("span");
  closeBtn.classList = "closefullscreen-btn";
  closeBtn.innerHTML = "&#x2715;";
  closeBtn.addEventListener("click", function () {
    ele.remove();
    cardParent.appendChild(ele);
    x.remove();
    document.querySelector(".content").style.display = "block";
  });

  document.querySelector(".content").style.display = "none";
  x.appendChild(ele);
  x.appendChild(closeBtn);
}

export function rlog(m) {
  console.log(`${new Date().toUTCString()} :: ${m}`);
}

export function llog(m) {
  console.log(`${new Date().toUTCString()} :: ${m}`);
}

let types;

class CAR {
  constructor() {
    get("/api/types")
      .then((x) => x.json)
      .then((xtypes) => {
        types = xtypes;
      });
    aSync = true;
  }
  setUpAlertHandler(f) {
    alertHandler = f;
  }
  alertx(type, msg) {
    alertHandler({
      type,
      message: msg
    })
  }
  info(msg) {
    this.alertx("info", msg)
  }
  success(msg) {
    this.alertx("success", msg)
  }
  warn(msg) {
    this.alertx("warning", msg)
  }
  error(msg) {
    this.alertx("error", msg)
  }
  currentUid() {
    if (!getCurrentUser())
      return undefined
    return getCurrentUser().split(':')[0];
  }
  isLoggedIn() {
    return getCurrentUser()
  }
  register(e, u, p) {
    return post(
      "/api/auth/register",
      {
        userName: u, email: e, password: p
      }
    )
  }
  login(u, p) {
    return post(
      "/api/auth/login",
      {},
      { Authorization: `Basic ${btoa(u + ":" + p)}` }
    ).then(x => {
      console.log(x)
      setCookie(USER_KEY, x.headers.authinfo)
    });
  }
  testLogin() {
    return post("/api/test-login")
  }
  logout() {
    eraseCookie(USER_KEY);
    eraseCookie(TOK_KEY);
    return post("/api/auth/logout");
  }
  regexMatch(rule, txts) {
    return post("/api/regex/match", { rule, txts }).then(x => x.json)
  }
  regexMatchV2(regex, txts) {
    return post("/api/v2/regex/match", { regex, txts }).then(x => x.json)
  }
  getLogGroupTypes() {
    return get("/api/log-type-groups")
      .then((x) => x.json)
      .then((grps) => grps.map((grp) => new LogTypeGroup(grp)));
  }
  getLogTypes(group) {
    return get(`/api/log-type-groups/${group}/logtypes`)
      .then((x) => x.json)
      .then((typs) => typs.map((typ) => new LogType(group, typ)));
  }
  getLogType(group, type) {
    return get(`/api/log-type-groups/${group}/logtypes/${type}`)
      .then((x) => new LogType(group, x.json))
  }
  getRules(group, type) {
    return get(`/api/log-type-groups/${group}/logtypes/${type}/rules`)
      .then((x) => x.json)
      .then((rules) => rules.map((rule) => new Rule(group, type, rule)));
  }
  getRegexTokens() {
    if (!this.regexTokens) {
      this.regexTokens = get(`/api/regex/tokens`).then(x => x.json)
    }
    return this.regexTokens
  }
  saveRegexToken(tok) {
    return post(`/api/regex/tokens`, {
      group: tok.grpKey,
      key: tok.tokKey,
      token: tok
    })
  }
  saveLogGroup(group) {
    let p = { name: group.name, description: group.description };
    return post(`/api/log-type-groups`, p).then(() =>
      this.success("saved the log type group")
    );
  }
  saveLogType(typ) {
    return post(`/api/log-type-groups/${typ.group}/logtypes`, typ).then(() =>
      this.success("saved the log type")
    );
  }
  saveRule(rule) {
    return post(
      `/api/log-type-groups/${rule.lgroup}/logtypes/${rule.ltype}/rules`,
      rule
    );
  }
  deleteRule(rule) {
    return delet(
      `/api/log-type-groups/${rule.lgroup}/logtypes/${rule.ltype}/rules/${rule.name}/`
    )
  }
  saveSearchView(view) {
    return post(
      `/api/search-views`, view
    )
  }
  getSearchViews() {
    return get(`/api/search-views`).then(x => x.json)
  }
  getTypes() {
    return types;
  }
  getRecordReaders() {
    return types.recordreader.subTypes.map((t) => types[t]);
  }
  getDetectorTypes() {
    return types.detector.subTypes.map((t) => types[t]);
  }
  getDatasets(q, offset, pageSize) {
    return get(`/api/datasets?q=${encodeURIComponent(q || "")}&offset=${offset}&pageSize=${pageSize}`).then((y) => {
      let x = y.json.map((d) => new Dataset(d)).sort((a, b) => a.title.localeCompare(b.title));
      console.log(x)
      return x
    });
  }
  getAllDatasets() {
    return get("/api/datasets/all/datasets").then((y) => {
      let x = y.json.sort((a, b) => a.name.localeCompare(b.name));
      console.log(x)
      return x
    });
  }
  getDataset(id) {
    rlog(`fetching the dataset definition: ${id}`);
    return get(`/api/datasets/${id}`).then((x) => new Dataset({ loaded: true, ...x.json }));
  }
  getFields(dsetid) {
    return get(`/api/datasets/${dsetid}/indexfields`).then(x => { return x.json })
  }
  analyze(id) {
    return post(`/api/datasets/${id}/analyze`);
  }
  analyzeFile(id, file) {
    return post(`/api/datasets/${id}/analyzefile?file=${encodeURIComponent(file)}`);
  }
  downloadFiles(id, files) {
    var link = document.createElement("a");
    let q = new URLSearchParams({file: files}).toString()
    link.href = `/api/datasets/${id}/downloadfile?${q}`;
    link.download = 'a'
    link.click();
  }
  status(id) {
    return get(`/api/datasets/${id}/status`).then((x) => x.json);
  }
  query(dsetid, q) {
    return post(`/api/datasets/${dsetid}/query`, q)
      .then((x) => x.json)
      .then((data) => data.map((x) => x.data));
  }
  getStoreSchema(dsetid, storetype) {
    return get(`/api/datasets/${dsetid}/schema/${storetype}`).then(x => x.json)
  }
  sql(dsetid, q) {
    if (!this.sqlNum)
      this.sqlNum = 0;
    let sqlNum = this.sqlNum++;
    rlog(`Executing SQL query: \n${q}`)
    console.time(`sql exectime ${sqlNum}`);
    return post(`/api/datasets/${dsetid}/query/sql?sql=${encodeURIComponent(q)}`)
      .then((x) => {
        console.timeEnd(`sql exectime ${sqlNum}`);
        return x.json
      })
  }
  getFTypes() {
    return types.fieldtype.subTypes.map((t) => types[t]);
  }
  cleanup(dsetid) {
    return delet(`/api/datasets/${dsetid}/cleanup`);
  }
  getChartTypes() {
    return types.visualization.subTypes.map((t) => types[t]);
  }
  saveDashBoard(dboard) {
    return post("/api/dashboards", dboard);
  }
  getDashboards(grp, typ) {
    return get(`/api/dashboards?group=${grp || ""}&type=${typ || ""}`).then(
      (x) => x.json
    );
  }
  createDataset(ds) {
    return post("/api/datasets", ds).then(x => x.json);
  }
  upload(dsetid, file, logTypeGroup, logType, updateProgress) {
    let fd = new FormData();
    fd.append("file", file, file.name);
    let hints = ''
    if (logTypeGroup) {
      hints += `?logTypeGroup=${encodeURIComponent(logTypeGroup)}`
      if (logType) {
        hints += `&logType=${encodeURIComponent(logType)}`
      }
    }
    return postFile(`/api/datasets/${dsetid}/files${hints}`, fd, {}, updateProgress).then(x => {
      car.info(`${file.name} uploaded`)
      return x
    });
  }
  searchLog(dsetid, q) {
    return post(`/api/datasets/${dsetid}/search`, q).then((x) => x.json);
  }
  sync() {
    return post(`/api/log-type-groups/sync`)
  }
  randColor() {
    var letters = "0123456789ABCDEF";
    var color = "#";
    for (var i = 0; i < 6; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
  }

  savePinned(datasets) {
    return post('/api/user-config/pinned', datasets)
  }

  getPinned() {
    return get('/api/user-config/pinned').then(resp => resp.json)
  }

  unPinDataset(id) {
    return delet(`/api/user-config/${id}/unpin`);
  }

  pinDataset(id) {
    return post(`/api/user-config/${id}/pin`)
  }

  fileOpened(dsetid, report, path) {
    let hist = JSON.parse(window.localStorage.getItem('history') || "[]")
    let link = `/ui/datasets/${dsetid}/reports/${report}/${path}`
    let idx = hist.findIndex(x => x.link == link)
    if (idx != -1)
      hist.splice(idx, 1)
    hist.unshift({ link, label: `${dsetid}/${path}`, type: 'file' })
    window.localStorage.setItem(`${this.currentUid()}-history`, JSON.stringify(hist))
  }

  getHistory() {
    return JSON.parse(window.localStorage.getItem(`${this.currentUid()}-history`) || "[]")
  }

  evalTemplateStringWithCtxt(template, ctxt) {
    function ctxtEvaluator(varList) {
      let js = `
        (function() {
          try {
            car.enterVAccessBlock()
            ${varList
              .map((k) => `let ${k} = ctxt.${k}`)
              .join("\n")
            }
            return \`${template}\`
          } finally {
            car.exitVAccessBlock()
          }
        })()
      `;
      return eval(js)
    }
    let varsNeeded = [], resolved = false
    while (!resolved) {
      try {
        resolved = ctxtEvaluator(varsNeeded)
      } catch (e) {
        if (e.name != 'ReferenceError')
          throw e
        let varName = e.message.split(' ')[0]
        if (!(varName in ctxt))
          throw e
        varsNeeded.push(varName)
        console.log(`vars needed -  ${varsNeeded}`)
      }
    }
    return resolved
  }

  loadPlugins() {
    if (this.plugins)
      return this.plugins
    this.plugins = get('/api/plugins').then(resp => {
      let proms = resp.json.filter(p => !p.startsWith('.')).map(plugname => get(`/api/plugins/${plugname}`))
      return Promise.allSettled(proms).then(pluginDefs => {
        console.log(pluginDefs)
        return pluginDefs.map(pd => {
          try {
            let plug = eval(pd.value.response)()
            plug.init(this)
            return plug
          } catch (e) {
            console.error(e)
          }
        }).filter(f => f != undefined)
      })
    })
    return this.plugins
  }

  // global place which holds the visualization being updated
  getCurrentUpdatingVisualization() {
    return this.updatingVisualization
  }

  setCurrentUpdatingVisualization(vis) {
    return this.updatingVisualization = vis
  }

  // blocks which access the varaibles need to call this before accessing this
  enterVAccessBlock() {
    this.accessingVariables = true
  }

  exitVAccessBlock() {
    this.accessingVariables = false
  }

  isVAccessGoingOn() {
    return this.accessingVariables
  }

  copyFormatted(html) {
    let clipboardDiv = document.createElement("div");
    clipboardDiv.style.fontSize = "12pt"; // Prevent zooming on iOS
    // Reset box model
    clipboardDiv.style.border = "0";
    clipboardDiv.style.padding = "0";
    clipboardDiv.style.margin = "0";
    // Move element out of screen
    clipboardDiv.style.position = "fixed";
    clipboardDiv.style["right"] = "-9999px";
    clipboardDiv.style.top =
      (window.pageYOffset || document.documentElement.scrollTop) + "px";
    // more hiding
    clipboardDiv.setAttribute("readonly", "");
    clipboardDiv.style.opacity = 0;
    clipboardDiv.style.pointerEvents = "none";
    clipboardDiv.style.zIndex = -1;
    clipboardDiv.setAttribute("tabindex", "0"); // so it can be focused
    clipboardDiv.innerHTML = "";
    document.body.appendChild(clipboardDiv);

    clipboardDiv.innerHTML = html;
    var focused = document.activeElement;
    clipboardDiv.focus();

    window.getSelection().removeAllRanges();
    var range = document.createRange();
    range.setStartBefore(clipboardDiv.firstChild);
    range.setEndAfter(clipboardDiv.lastChild);
    window.getSelection().addRange(range);
    try {
      if (document.execCommand("copy")) {
        console.log("copied");
      } else console.log("execCommand returned false !");
    } catch (err) {
      console.log("execCommand failed ! exception " + err);
    }
    focused.focus();
  }

  render(name, spec, elemCreated, container) {
    return render(name, spec, elemCreated, container)
  }

}

export let car = new CAR();
export default class dummyclass { }

// native html apis
export function render(name, spec, elemCreated, container) {
  let x = _render(name, spec, elemCreated, container);
  _fireRenderedCallBacks(spec);
  return x;
}

function _fireRenderedCallBacks(spec) {
  if (spec && spec.evnts && spec.evnts.rendered)
    spec.evnts.rendered(spec._______elem)
  delete spec['_______elem']
  spec.children && spec.children.forEach(child => _fireRenderedCallBacks(child))
}

function _render(name, spec, elemCreated, container) {
  if (Array.isArray(spec)) {
    spec.forEach(s => render(name, s, elemCreated, container))
    return container
  }
  let e;
  if (!spec.preBuilt) {
    e = document.createElement(spec.ele);
  } else {
    e = spec.ele;
  }
  spec._______elem = e;
  spec.iden && elemCreated && elemCreated(spec.iden, e)
  if (spec.text) e.innerText = spec.text;
  if (spec.html) e.innerHTML = spec.html;
  if (spec.classList) {
    spec.classList.split(/\s+/).map(x => e.classList.add(x[0] == '$' ? x.substring(1) : `${name}-${x}`))
  }
  spec.styles && Object.keys(spec.styles).forEach(key => { e.style[key] = spec.styles[key] })
  spec.evnts && Object.keys(spec.evnts).forEach(key => { e.addEventListener(key, spec.evnts[key]) })
  if (spec.children) {
    if (spec.children instanceof Function) spec.children().forEach(x => e.appendChild(x))
    else spec.children.forEach(child => _render(name, child, elemCreated, e))
  }
  if (spec.value) e.value = spec.value
  if (spec.title) e.title = spec.title
  spec.attribs && Object.keys(spec.attribs).forEach(key => {
    e[key] = spec.attribs[key]
  })
  if (container) {
    let lbl;
    if (spec.label || spec.postlabel) {
      let rgid = "id_" + Math.random();
      e.id = rgid
      lbl = document.createElement('label')
      spec.labelStyle && (lbl.style = spec.labelStyle)
      lbl.innerHTML = spec.label || spec.postlabel
      lbl.setAttribute('for', rgid)
    }
    if (spec.label) container.appendChild(lbl)
    container.appendChild(e)
    if (spec.postlabel) container.appendChild(lbl)
    return container;
  }
  return e;
}

export function Field(name, visible, wrap, align, decodeAs, decodeFunc, showDesc, stats, queried) {
  (this.name = name),
    (this.wrap = wrap),
    (this.visible = visible),
    (this.showDesc = showDesc),
    (this.decodeAs = decodeAs || "default"),
    (this.decodeFunc = decodeFunc),
    (this.align = align || "left"),
    (this.stats = stats),
    (this.queried = queried);
}

export let decodeFuncs = {
  date: function (msg, field) {
    let key = field.name;
    try {
      return new Date(+msg[key]).toISOString();
    } catch {
      return msg[key];
    }
  },
  escapedstring: function (msg, field) {
    let key = field.name;
    try {
      let x = `x => ${msg[key]}`
      return eval(x)()
    } catch {
      return msg[key]
    }
  },
  default: (msg, field) => msg[field.name],
  custom: (msg, field) => {
    return eval(field.decodeFunc)(msg[field.name], msg)
  }
};