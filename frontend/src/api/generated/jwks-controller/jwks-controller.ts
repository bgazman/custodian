/**
 * Generated by orval v7.4.1 🍺
 * Do not edit manually.
 * Your API
 * OpenAPI spec version: 1.0.0
 */
import {
  useInfiniteQuery,
  useQuery
} from '@tanstack/react-query'
import type {
  DataTag,
  DefinedInitialDataOptions,
  DefinedUseInfiniteQueryResult,
  DefinedUseQueryResult,
  InfiniteData,
  QueryFunction,
  QueryKey,
  UndefinedInitialDataOptions,
  UseInfiniteQueryOptions,
  UseInfiniteQueryResult,
  UseQueryOptions,
  UseQueryResult
} from '@tanstack/react-query'
import type {
  GetJwks200,
  GetJwks500,
  GetOpenIdConfiguration200,
  GetOpenIdConfiguration500
} from '.././model'
import { customFetcher } from '../../common/SecureApiClient';



export const getOpenIdConfiguration = (
    
 signal?: AbortSignal
) => {
      
      
      return customFetcher<GetOpenIdConfiguration200>(
      {url: `http://localhost:8080/.well-known/openid-configuration`, method: 'GET', signal
    },
      );
    }
  

export const getGetOpenIdConfigurationQueryKey = () => {
    return [`http://localhost:8080/.well-known/openid-configuration`] as const;
    }

    
export const getGetOpenIdConfigurationInfiniteQueryOptions = <TData = InfiniteData<Awaited<ReturnType<typeof getOpenIdConfiguration>>>, TError = GetOpenIdConfiguration500>( options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getGetOpenIdConfigurationQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getOpenIdConfiguration>>> = ({ signal }) => getOpenIdConfiguration(signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseInfiniteQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetOpenIdConfigurationInfiniteQueryResult = NonNullable<Awaited<ReturnType<typeof getOpenIdConfiguration>>>
export type GetOpenIdConfigurationInfiniteQueryError = GetOpenIdConfiguration500


export function useGetOpenIdConfigurationInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getOpenIdConfiguration>>>, TError = GetOpenIdConfiguration500>(
  options: { query:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getOpenIdConfiguration>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetOpenIdConfigurationInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getOpenIdConfiguration>>>, TError = GetOpenIdConfiguration500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getOpenIdConfiguration>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetOpenIdConfigurationInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getOpenIdConfiguration>>>, TError = GetOpenIdConfiguration500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useGetOpenIdConfigurationInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getOpenIdConfiguration>>>, TError = GetOpenIdConfiguration500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetOpenIdConfigurationInfiniteQueryOptions(options)

  const query = useInfiniteQuery(queryOptions) as  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const getGetOpenIdConfigurationQueryOptions = <TData = Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError = GetOpenIdConfiguration500>( options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getGetOpenIdConfigurationQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getOpenIdConfiguration>>> = ({ signal }) => getOpenIdConfiguration(signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetOpenIdConfigurationQueryResult = NonNullable<Awaited<ReturnType<typeof getOpenIdConfiguration>>>
export type GetOpenIdConfigurationQueryError = GetOpenIdConfiguration500


export function useGetOpenIdConfiguration<TData = Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError = GetOpenIdConfiguration500>(
  options: { query:Partial<UseQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getOpenIdConfiguration>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetOpenIdConfiguration<TData = Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError = GetOpenIdConfiguration500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getOpenIdConfiguration>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetOpenIdConfiguration<TData = Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError = GetOpenIdConfiguration500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useGetOpenIdConfiguration<TData = Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError = GetOpenIdConfiguration500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getOpenIdConfiguration>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetOpenIdConfigurationQueryOptions(options)

  const query = useQuery(queryOptions) as  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const getJwks = (
    
 signal?: AbortSignal
) => {
      
      
      return customFetcher<GetJwks200>(
      {url: `http://localhost:8080/.well-known/jwks.json`, method: 'GET', signal
    },
      );
    }
  

export const getGetJwksQueryKey = () => {
    return [`http://localhost:8080/.well-known/jwks.json`] as const;
    }

    
export const getGetJwksInfiniteQueryOptions = <TData = InfiniteData<Awaited<ReturnType<typeof getJwks>>>, TError = GetJwks500>( options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getGetJwksQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getJwks>>> = ({ signal }) => getJwks(signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseInfiniteQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetJwksInfiniteQueryResult = NonNullable<Awaited<ReturnType<typeof getJwks>>>
export type GetJwksInfiniteQueryError = GetJwks500


export function useGetJwksInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getJwks>>>, TError = GetJwks500>(
  options: { query:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getJwks>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetJwksInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getJwks>>>, TError = GetJwks500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getJwks>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetJwksInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getJwks>>>, TError = GetJwks500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useGetJwksInfinite<TData = InfiniteData<Awaited<ReturnType<typeof getJwks>>>, TError = GetJwks500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetJwksInfiniteQueryOptions(options)

  const query = useInfiniteQuery(queryOptions) as  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const getGetJwksQueryOptions = <TData = Awaited<ReturnType<typeof getJwks>>, TError = GetJwks500>( options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getGetJwksQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getJwks>>> = ({ signal }) => getJwks(signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetJwksQueryResult = NonNullable<Awaited<ReturnType<typeof getJwks>>>
export type GetJwksQueryError = GetJwks500


export function useGetJwks<TData = Awaited<ReturnType<typeof getJwks>>, TError = GetJwks500>(
  options: { query:Partial<UseQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getJwks>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetJwks<TData = Awaited<ReturnType<typeof getJwks>>, TError = GetJwks500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getJwks>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetJwks<TData = Awaited<ReturnType<typeof getJwks>>, TError = GetJwks500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useGetJwks<TData = Awaited<ReturnType<typeof getJwks>>, TError = GetJwks500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getJwks>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetJwksQueryOptions(options)

  const query = useQuery(queryOptions) as  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



