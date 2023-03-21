/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-23 11:34:54
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-01 17:04:24
 * @Description: file content
 *
 */
import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'
import { IGetHistoryOrder, IGetHistoryOrderRusult } from './type'

const api = useApi()

enum projectControlApi {
  QueryQr = '/client/surPayment/createNative',
  QueryStatus = '/client/surPayment/queryOrder',
  ShutDowOrder = '/client/surPayment/closeOrder',
  GetOrderInfo = '/client/surPayment/queryPayment',
  GetHistoryOrder = '/client/surPayment/queryAllOrder',
  GetIntegetMagnification = '/client/surTopUp',
}

export const queryQrApi = (totalFee: number): Promise<IDataType<any>> => {
  return api.get(projectControlApi.QueryQr, {
    params: {
      totalFee,
    },
  })
}

export const queryStatusApi = (outTradeNo: string): Promise<IDataType<any>> => {
  return api.get(projectControlApi.QueryStatus, {
    params: {
      outTradeNo,
    },
  })
}

export const shutDowOrderApi = (outTradeNo: string): Promise<IDataType<any>> => {
  return api.get(projectControlApi.ShutDowOrder, {
    params: {
      outTradeNo,
    },
  })
}

export const getOrderInfoApi = (outTradeNo: string): Promise<IDataType<any>> => {
  return api.get(projectControlApi.GetOrderInfo, {
    params: {
      outTradeNo,
    },
  })
}

export const getHistoryOrderApi = (
  data: IGetHistoryOrder
): Promise<IDataType<IGetHistoryOrderRusult>> => {
  return api.get(projectControlApi.GetHistoryOrder, {
    params: data,
  })
}

export const getIntegetMagnificationApi = (): Promise<IDataType<any>> => {
  return api.get(projectControlApi.GetIntegetMagnification)
}
