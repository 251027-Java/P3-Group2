/**
 * This file was created by Claude Sonnet 4.5
 */
const { merge } = require("webpack-merge");
const singleSpaDefaults = require("webpack-config-single-spa-react-ts");

module.exports = (webpackConfigEnv, argv) => {
  const defaultConfig = singleSpaDefaults({
    orgName: "marketplace",
    projectName: "mfe-react-users",
    webpackConfigEnv,
    argv,
  });

  return merge(defaultConfig, {
    // Hot Module Replacement
    devServer: {
      port: 4202,
      hot: true,
      historyApiFallback: true,
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
        "Access-Control-Allow-Headers": "X-Requested-With, content-type, Authorization",
      },
      client: {
        overlay: {
          errors: true,
          warnings: false,
        },
      },
    },
    // Externals for shared dependencies
    externals: ["react", "react-dom", "react-router-dom", "single-spa"],
  });
};
