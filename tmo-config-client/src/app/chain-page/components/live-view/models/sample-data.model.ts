export enum SampleDataType {
    MANUAL = 'manual',
    KAFKA = 'kafka',
    HDFS = 'hdfs'
}

export interface SampleDataModel {
    type: SampleDataType;
    source: string;
}
