class LogMessageStore extends Store {
  constructor(sd, vis) {
    super("logmsg");
    this.q = sd ? sd.q : {};
  }
  requiresUpdate(cl) {
    // cl => changed props list
  }
  getData(ctxt) {}
}
