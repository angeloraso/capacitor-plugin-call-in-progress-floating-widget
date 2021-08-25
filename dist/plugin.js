var capacitorCallInProgressFloatingWidget = (function (exports, core) {
    'use strict';

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

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

}({}, capacitorExports));
//# sourceMappingURL=plugin.js.map
