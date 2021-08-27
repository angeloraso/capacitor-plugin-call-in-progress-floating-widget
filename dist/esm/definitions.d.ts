export interface CallInProgressFloatingWidgetPlugin {
    show(data: {
        startTime: number;
    }): Promise<void>;
    hide(): Promise<{
        data: 'success' | 'error';
    }>;
}
