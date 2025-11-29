import Visualization from "./Visualization";
export default class LogMessages extends Visualization {
  constructor(a) {
    super(a);
    this.eventNames = [];
    this.dataStoreTypes = ["lucene"];
    this.lastOffset = -1;
    this.fQueried = []
    if (!this.customView)
      this.customView = { fields: [], name: 'custom' }
    this.viewName = 'custom'
  }

  setData(newData) {
    if (!this.view.data)
      this.view.data = []

    let equal = function (a, b) {
      if(a.length != b.length)
        return false;
      for (let i = 0; i < a.length; i++)
        if (a[i] != b[i])
          return false;
      return true;
    }

    if (!newData.props)
      return

    let schemaChanged = !equal(this.fQueried, newData.props.fQueried)
    let queryChanged = (this.query != undefined) && this.query != newData.props.query
    let sortFieldChanged = this.sorted != newData.props.sortFields
    if (schemaChanged || queryChanged || sortFieldChanged)
      this.view.data = []

    if (+this.lastOffset < +newData.props.offset || schemaChanged || queryChanged || sortFieldChanged) {
      newData.forEach(nr => this.view.data.push(nr))
    }

    this.lastOffset = newData.props.offset
    this.fQueried = newData.props.fQueried
    this.sorted = newData.props.sortFields
    this.query = newData.props.query
    if (newData.props) {
      Object.entries(newData.props).forEach(([k, v]) => {
        this.view.data[k] = v
      })
    }
  }

  scroll() {
    return this.update()
  }

}
