const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  configureWebpack: {
    performance: {
      maxEntrypointSize: 650000,
      maxAssetSize: 350000
    }
  }
})
