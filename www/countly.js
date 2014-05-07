var cordova = require('cordova');

var Countly = function (key) {
  var count, sum, segment;
  if (typeof arguments[1] === 'number') {
    count = arguments[1];
    sum = arguments[2];
  } else {
    segment = arguments[1];
    count = arguments[2];
    sum = arguments[3];
  }
  
  var opts = {
      key: key || 'undefined',
      segment: segment,
      count: count,
      sum: sum
  };
  cordova.exec(Countly.defaultSuccessCallback, Countly.defaultErrorCallback, 'Countly', 'track', [opts]);
}

Countly.defaultSuccessCallback = function () {
  console.debug('send event track');
}

Countly.defaultErrorCallback = function (message) {
  console.error(message);
}

module.exports = Countly;