import { WebPlugin } from '@capacitor/core';
import type { CallInProgressFloatingWidgetPlugin } from './definitions';
export declare class CallInProgressFloatingWidgetWeb extends WebPlugin implements CallInProgressFloatingWidgetPlugin {
    show(data: {
        seconds: number;
    }): Promise<void>;
    hide(): Promise<{
        data: 'success' | 'error';
    }>;
}
