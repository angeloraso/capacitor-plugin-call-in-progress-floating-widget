import { registerPlugin } from '@capacitor/core';
const CallInProgressFloatingWidget = registerPlugin('CallInProgressFloatingWidget', {
    web: () => import('./web').then(m => new m.CallInProgressFloatingWidgetWeb()),
});
export * from './definitions';
export { CallInProgressFloatingWidget };
//# sourceMappingURL=index.js.map