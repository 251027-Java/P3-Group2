const { merge } = require("webpack-merge");
const singleSpaDefaults = require("webpack-config-single-spa-ts");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const path = require('path');

module.exports = (webpackConfigEnv, argv) => {
  const orgName = "marketplace";
  const isLocal = webpackConfigEnv && webpackConfigEnv.isLocal;

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
          directory: path.join(__dirname, 'src/styles'),
          publicPath: '/styles',
        }
      ]
    },
    plugins: [
      new HtmlWebpackPlugin({
        inject: false,
        template: "src/index.ejs",
        templateParameters: {
          isLocal,
          orgName,
        },
      }),
      // Copy static assets for production builds
      new CopyWebpackPlugin({
        patterns: [
          { from: "src/styles", to: "styles" },
          { from: "src/importmap.json", to: "importmap.json" },
        ],
      }),
    ],
  });
};
