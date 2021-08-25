export interface CallInProgressFloatingWidgetPlugin {
    show(data: {
        seconds: number;
    }): Promise<void>;
    hide(): Promise<{
        data: 'success' | 'error';
    }>;
}
