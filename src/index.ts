import { registerPlugin } from '@capacitor/core';

import type { CallInProgressFloatingWidgetPlugin } from './definitions';

const CallInProgressFloatingWidget = registerPlugin<CallInProgressFloatingWidgetPlugin>(
  'CallInProgressFloatingWidget',
  {
    web: () =>
      import('./web').then(m => new m.CallInProgressFloatingWidgetWeb()),
  },
);

export * from './definitions';
export { CallInProgressFloatingWidget };
