/**
 * This file was created by Claude Sonnet 4.5
 */
const { merge } = require("webpack-merge");
const singleSpaDefaults = require("webpack-config-single-spa-react-ts");
const { DefinePlugin } = require("webpack");
const dotenv = require('dotenv');
const path = require('path');

module.exports = (webpackConfigEnv, argv) => {
  const defaultConfig = singleSpaDefaults({
    orgName: "marketplace",
    projectName: "mfe-react-users",
    webpackConfigEnv,
    argv,
  });

  if (defaultConfig.mode === 'production') {
    dotenv.config({ path: '.env.production' });
  } else {
    dotenv.config();
  }

  // Build navbar as a separate entry if requested
  const isNavbarBuild = webpackConfigEnv && webpackConfigEnv.navbar;
  
  if (isNavbarBuild) {
    return merge(defaultConfig, {
      entry: path.resolve(process.cwd(), 'src/navbar-mfe-react-users.tsx'),
      output: {
        ...defaultConfig.output,
        filename: 'marketplace-mfe-react-navbar.js',
      },
      externals: ["react", "react-dom", "react-router-dom", "single-spa"],
      plugins: [
        new DefinePlugin({
          "process.env.API_URL": JSON.stringify(process.env.API_URL),
        })
      ]
    });
  }

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
    plugins: [
      new DefinePlugin({
        "process.env.API_URL": JSON.stringify(process.env.API_URL),
      })
    ]
  });
};
