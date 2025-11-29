const autoprefixer = require('autoprefixer')
const filterRules = require('postcss-filter-rules')

module.exports = {
  transpileDependencies: ["vuetify"],
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://localhost:9090'
      }
    }
  },
  css: {
    loaderOptions: {
      postcss: {
        plugins: [
          filterRules({
            filter: (selector) => {
              const re = new RegExp(/^(select)(\W|$)/, 'i')
              const exception = '.vue-global'
              return !re.test(selector) || selector.includes(exception)
            },
            keepAtRules: true
          }),
          autoprefixer
        ]
      }
    }
  }
};
