'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const CallInProgressFloatingWidget = core.registerPlugin('CallInProgressFloatingWidget', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.CallInProgressFloatingWidgetWeb()),
});

class CallInProgressFloatingWidgetWeb extends core.WebPlugin {
    async show(data) {
        console.log((data));
        return;
    }
    async hide() { return { data: 'success' }; }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    CallInProgressFloatingWidgetWeb: CallInProgressFloatingWidgetWeb
});

exports.CallInProgressFloatingWidget = CallInProgressFloatingWidget;
//# sourceMappingURL=plugin.cjs.js.map
