import { WebPlugin } from '@capacitor/core';
export class CallInProgressFloatingWidgetWeb extends WebPlugin {
    async show(data) {
        console.log((data));
        return;
    }
    async hide() { return { data: 'success' }; }
}
//# sourceMappingURL=web.js.map