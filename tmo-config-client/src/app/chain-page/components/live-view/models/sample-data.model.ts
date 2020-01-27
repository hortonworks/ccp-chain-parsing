export enum SampleDataType {
    MANUAL = 'manual',
    KAFKA = 'kafka',
    HDFS = 'hdfs'
}

export class SampleDataModel {
    type = SampleDataType.MANUAL;
    source: string;
}
