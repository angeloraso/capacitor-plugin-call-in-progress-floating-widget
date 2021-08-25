import { WebPlugin } from '@capacitor/core';

import type { CallInProgressFloatingWidgetPlugin } from './definitions';

export class CallInProgressFloatingWidgetWeb
  extends WebPlugin
  implements CallInProgressFloatingWidgetPlugin {
    async show(data: {seconds: number}): Promise<void> {
      console.log((data));
      return;
    }  
    async hide(): Promise<{ data: 'success' | 'error' }> {return {data: 'success'};}
}
