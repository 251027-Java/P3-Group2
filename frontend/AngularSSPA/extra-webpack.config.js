const singleSpaAngularWebpack = require('single-spa-angular/lib/webpack').default;

module.exports = (config, options) => {
  const singleSpaWebpackConfig = singleSpaAngularWebpack(config, options);

  // Align the SystemJS module name with the import map entry used by root-config.
  singleSpaWebpackConfig.output = {
    ...singleSpaWebpackConfig.output,
    library: "@marketplace/mfe-angular-cards",
  };

  return singleSpaWebpackConfig;
};
