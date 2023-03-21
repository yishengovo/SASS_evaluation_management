export interface IColumns {
  field: string
  title: string
  minwidth?: string
  slotName?: string
  otherOptions?: any
}
export interface ITable {
  columns: IColumns[]
  data: any[]
  otherOptions?: any
}
