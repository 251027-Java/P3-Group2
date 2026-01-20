const { merge } = require("webpack-merge");
const singleSpaDefaults = require("webpack-config-single-spa-ts");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const stylePath = require('path');

module.exports = (webpackConfigEnv, argv) => {
  const orgName = "marketplace";
  const defaultConfig = singleSpaDefaults({
    orgName,
    projectName: "root-config",
    webpackConfigEnv,
    argv,
    disableHtmlGeneration: true,
    outputSystemJS: true,
  });

  return merge(defaultConfig, {
    // Hot Module Replacement
    devServer: {
      port: 9000,
      hot: true,
      historyApiFallback: true,
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
        "Access-Control-Allow-Headers": "X-Requested-With, content-type, Authorization",
      },
      static: [
        {
          directory: stylePath.join(__dirname, 'src/styles'),
          publicPath: '/styles',
        }
      ]
      // client: {
      //   overlay: {
      //     errors: true,
      //     warnings: false,
      //   },
      // },
    },
    // modify the webpack config however you'd like to by adding to this object
    plugins: [
      new HtmlWebpackPlugin({
        inject: false,
        template: "src/index.ejs",
        templateParameters: {
          isLocal: webpackConfigEnv && webpackConfigEnv.isLocal,
          orgName,
        },
      }),
    ],
  });
};
