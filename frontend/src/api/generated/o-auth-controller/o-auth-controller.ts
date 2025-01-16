/**
 * Generated by orval v7.4.1 🍺
 * Do not edit manually.
 * Your API
 * OpenAPI spec version: 1.0.0
 */
import {
  useInfiniteQuery,
  useMutation,
  useQuery
} from '@tanstack/react-query'
import type {
  DataTag,
  DefinedInitialDataOptions,
  DefinedUseInfiniteQueryResult,
  DefinedUseQueryResult,
  InfiniteData,
  MutationFunction,
  QueryFunction,
  QueryKey,
  UndefinedInitialDataOptions,
  UseInfiniteQueryOptions,
  UseInfiniteQueryResult,
  UseMutationOptions,
  UseMutationResult,
  UseQueryOptions,
  UseQueryResult
} from '@tanstack/react-query'
import type {
  Authorize200,
  Authorize500,
  AuthorizeParams,
  AuthorizeRequest,
  Introspect200,
  Introspect500,
  IntrospectParams,
  Login200,
  Login500,
  RevokeToken200,
  RevokeToken500,
  Token200,
  Token500,
  TokenRequest,
  Userinfo200,
  Userinfo500
} from '../index.schemas'
import { customFetcher } from '../../common/SecureApiClient';



export const token = (
    tokenRequest: TokenRequest,
 signal?: AbortSignal
) => {
      
      
      return customFetcher<Token200>(
      {url: `http://localhost:8080/oauth/token`, method: 'POST',
      headers: {'Content-Type': 'application/json', },
      data: tokenRequest, signal
    },
      );
    }
  


export const getTokenMutationOptions = <TData = Awaited<ReturnType<typeof token>>, TError = Token500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: TokenRequest}, TContext>, }
) => {
const mutationKey = ['token'];
const {mutation: mutationOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof token>>, {data: TokenRequest}> = (props) => {
          const {data} = props ?? {};

          return  token(data,)
        }

        


  return  { mutationFn, ...mutationOptions } as UseMutationOptions<TData, TError,{data: TokenRequest}, TContext>}

    export type TokenMutationResult = NonNullable<Awaited<ReturnType<typeof token>>>
    export type TokenMutationBody = TokenRequest
    export type TokenMutationError = Token500

    export const useToken = <TData = Awaited<ReturnType<typeof token>>, TError = Token500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: TokenRequest}, TContext>, }
): UseMutationResult<
        TData,
        TError,
        {data: TokenRequest},
        TContext
      > => {

      const mutationOptions = getTokenMutationOptions(options);

      return useMutation(mutationOptions);
    }
    export const revokeToken = (
    revokeTokenBody: string,
 signal?: AbortSignal
) => {
      
      
      return customFetcher<RevokeToken200>(
      {url: `http://localhost:8080/oauth/revoke`, method: 'POST',
      headers: {'Content-Type': 'application/json', },
      data: revokeTokenBody, signal
    },
      );
    }
  


export const getRevokeTokenMutationOptions = <TData = Awaited<ReturnType<typeof revokeToken>>, TError = RevokeToken500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: string}, TContext>, }
) => {
const mutationKey = ['revokeToken'];
const {mutation: mutationOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof revokeToken>>, {data: string}> = (props) => {
          const {data} = props ?? {};

          return  revokeToken(data,)
        }

        


  return  { mutationFn, ...mutationOptions } as UseMutationOptions<TData, TError,{data: string}, TContext>}

    export type RevokeTokenMutationResult = NonNullable<Awaited<ReturnType<typeof revokeToken>>>
    export type RevokeTokenMutationBody = string
    export type RevokeTokenMutationError = RevokeToken500

    export const useRevokeToken = <TData = Awaited<ReturnType<typeof revokeToken>>, TError = RevokeToken500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: string}, TContext>, }
): UseMutationResult<
        TData,
        TError,
        {data: string},
        TContext
      > => {

      const mutationOptions = getRevokeTokenMutationOptions(options);

      return useMutation(mutationOptions);
    }
    export const login = (
    authorizeRequest: AuthorizeRequest,
 signal?: AbortSignal
) => {
      
      
      return customFetcher<Login200>(
      {url: `http://localhost:8080/oauth/login`, method: 'POST',
      headers: {'Content-Type': 'application/json', },
      data: authorizeRequest, signal
    },
      );
    }
  


export const getLoginMutationOptions = <TData = Awaited<ReturnType<typeof login>>, TError = Login500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: AuthorizeRequest}, TContext>, }
) => {
const mutationKey = ['login'];
const {mutation: mutationOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof login>>, {data: AuthorizeRequest}> = (props) => {
          const {data} = props ?? {};

          return  login(data,)
        }

        


  return  { mutationFn, ...mutationOptions } as UseMutationOptions<TData, TError,{data: AuthorizeRequest}, TContext>}

    export type LoginMutationResult = NonNullable<Awaited<ReturnType<typeof login>>>
    export type LoginMutationBody = AuthorizeRequest
    export type LoginMutationError = Login500

    export const useLogin = <TData = Awaited<ReturnType<typeof login>>, TError = Login500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: AuthorizeRequest}, TContext>, }
): UseMutationResult<
        TData,
        TError,
        {data: AuthorizeRequest},
        TContext
      > => {

      const mutationOptions = getLoginMutationOptions(options);

      return useMutation(mutationOptions);
    }
    export const userinfo = (
    
 signal?: AbortSignal
) => {
      
      
      return customFetcher<Userinfo200>(
      {url: `http://localhost:8080/oauth/userinfo`, method: 'GET', signal
    },
      );
    }
  

export const getUserinfoQueryKey = () => {
    return [`http://localhost:8080/oauth/userinfo`] as const;
    }

    
export const getUserinfoInfiniteQueryOptions = <TData = InfiniteData<Awaited<ReturnType<typeof userinfo>>>, TError = Userinfo500>( options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getUserinfoQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof userinfo>>> = ({ signal }) => userinfo(signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseInfiniteQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type UserinfoInfiniteQueryResult = NonNullable<Awaited<ReturnType<typeof userinfo>>>
export type UserinfoInfiniteQueryError = Userinfo500


export function useUserinfoInfinite<TData = InfiniteData<Awaited<ReturnType<typeof userinfo>>>, TError = Userinfo500>(
  options: { query:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof userinfo>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useUserinfoInfinite<TData = InfiniteData<Awaited<ReturnType<typeof userinfo>>>, TError = Userinfo500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof userinfo>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useUserinfoInfinite<TData = InfiniteData<Awaited<ReturnType<typeof userinfo>>>, TError = Userinfo500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useUserinfoInfinite<TData = InfiniteData<Awaited<ReturnType<typeof userinfo>>>, TError = Userinfo500>(
  options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getUserinfoInfiniteQueryOptions(options)

  const query = useInfiniteQuery(queryOptions) as  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const getUserinfoQueryOptions = <TData = Awaited<ReturnType<typeof userinfo>>, TError = Userinfo500>( options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getUserinfoQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof userinfo>>> = ({ signal }) => userinfo(signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type UserinfoQueryResult = NonNullable<Awaited<ReturnType<typeof userinfo>>>
export type UserinfoQueryError = Userinfo500


export function useUserinfo<TData = Awaited<ReturnType<typeof userinfo>>, TError = Userinfo500>(
  options: { query:Partial<UseQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof userinfo>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useUserinfo<TData = Awaited<ReturnType<typeof userinfo>>, TError = Userinfo500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof userinfo>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useUserinfo<TData = Awaited<ReturnType<typeof userinfo>>, TError = Userinfo500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useUserinfo<TData = Awaited<ReturnType<typeof userinfo>>, TError = Userinfo500>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof userinfo>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getUserinfoQueryOptions(options)

  const query = useQuery(queryOptions) as  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const introspect = (
    params: IntrospectParams,
 signal?: AbortSignal
) => {
      
      
      return customFetcher<Introspect200>(
      {url: `http://localhost:8080/oauth/introspect`, method: 'GET',
        params, signal
    },
      );
    }
  

export const getIntrospectQueryKey = (params: IntrospectParams,) => {
    return [`http://localhost:8080/oauth/introspect`, ...(params ? [params]: [])] as const;
    }

    
export const getIntrospectInfiniteQueryOptions = <TData = InfiniteData<Awaited<ReturnType<typeof introspect>>>, TError = Introspect500>(params: IntrospectParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getIntrospectQueryKey(params);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof introspect>>> = ({ signal }) => introspect(params, signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseInfiniteQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type IntrospectInfiniteQueryResult = NonNullable<Awaited<ReturnType<typeof introspect>>>
export type IntrospectInfiniteQueryError = Introspect500


export function useIntrospectInfinite<TData = InfiniteData<Awaited<ReturnType<typeof introspect>>>, TError = Introspect500>(
 params: IntrospectParams, options: { query:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof introspect>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useIntrospectInfinite<TData = InfiniteData<Awaited<ReturnType<typeof introspect>>>, TError = Introspect500>(
 params: IntrospectParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof introspect>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useIntrospectInfinite<TData = InfiniteData<Awaited<ReturnType<typeof introspect>>>, TError = Introspect500>(
 params: IntrospectParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useIntrospectInfinite<TData = InfiniteData<Awaited<ReturnType<typeof introspect>>>, TError = Introspect500>(
 params: IntrospectParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getIntrospectInfiniteQueryOptions(params,options)

  const query = useInfiniteQuery(queryOptions) as  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const getIntrospectQueryOptions = <TData = Awaited<ReturnType<typeof introspect>>, TError = Introspect500>(params: IntrospectParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getIntrospectQueryKey(params);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof introspect>>> = ({ signal }) => introspect(params, signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type IntrospectQueryResult = NonNullable<Awaited<ReturnType<typeof introspect>>>
export type IntrospectQueryError = Introspect500


export function useIntrospect<TData = Awaited<ReturnType<typeof introspect>>, TError = Introspect500>(
 params: IntrospectParams, options: { query:Partial<UseQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof introspect>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useIntrospect<TData = Awaited<ReturnType<typeof introspect>>, TError = Introspect500>(
 params: IntrospectParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof introspect>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useIntrospect<TData = Awaited<ReturnType<typeof introspect>>, TError = Introspect500>(
 params: IntrospectParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useIntrospect<TData = Awaited<ReturnType<typeof introspect>>, TError = Introspect500>(
 params: IntrospectParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof introspect>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getIntrospectQueryOptions(params,options)

  const query = useQuery(queryOptions) as  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const authorize = (
    params: AuthorizeParams,
 signal?: AbortSignal
) => {
      
      
      return customFetcher<Authorize200>(
      {url: `http://localhost:8080/oauth/authorize`, method: 'GET',
        params, signal
    },
      );
    }
  

export const getAuthorizeQueryKey = (params: AuthorizeParams,) => {
    return [`http://localhost:8080/oauth/authorize`, ...(params ? [params]: [])] as const;
    }

    
export const getAuthorizeInfiniteQueryOptions = <TData = InfiniteData<Awaited<ReturnType<typeof authorize>>>, TError = Authorize500>(params: AuthorizeParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getAuthorizeQueryKey(params);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof authorize>>> = ({ signal }) => authorize(params, signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseInfiniteQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type AuthorizeInfiniteQueryResult = NonNullable<Awaited<ReturnType<typeof authorize>>>
export type AuthorizeInfiniteQueryError = Authorize500


export function useAuthorizeInfinite<TData = InfiniteData<Awaited<ReturnType<typeof authorize>>>, TError = Authorize500>(
 params: AuthorizeParams, options: { query:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof authorize>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useAuthorizeInfinite<TData = InfiniteData<Awaited<ReturnType<typeof authorize>>>, TError = Authorize500>(
 params: AuthorizeParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof authorize>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useAuthorizeInfinite<TData = InfiniteData<Awaited<ReturnType<typeof authorize>>>, TError = Authorize500>(
 params: AuthorizeParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useAuthorizeInfinite<TData = InfiniteData<Awaited<ReturnType<typeof authorize>>>, TError = Authorize500>(
 params: AuthorizeParams, options?: { query?:Partial<UseInfiniteQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>>, }

  ):  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getAuthorizeInfiniteQueryOptions(params,options)

  const query = useInfiniteQuery(queryOptions) as  UseInfiniteQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



export const getAuthorizeQueryOptions = <TData = Awaited<ReturnType<typeof authorize>>, TError = Authorize500>(params: AuthorizeParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>>, }
) => {

const {query: queryOptions} = options ?? {};

  const queryKey =  queryOptions?.queryKey ?? getAuthorizeQueryKey(params);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof authorize>>> = ({ signal }) => authorize(params, signal);

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData> & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type AuthorizeQueryResult = NonNullable<Awaited<ReturnType<typeof authorize>>>
export type AuthorizeQueryError = Authorize500


export function useAuthorize<TData = Awaited<ReturnType<typeof authorize>>, TError = Authorize500>(
 params: AuthorizeParams, options: { query:Partial<UseQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>> & Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof authorize>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useAuthorize<TData = Awaited<ReturnType<typeof authorize>>, TError = Authorize500>(
 params: AuthorizeParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>> & Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof authorize>>,
          TError,
          TData
        > , 'initialData'
      >, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useAuthorize<TData = Awaited<ReturnType<typeof authorize>>, TError = Authorize500>(
 params: AuthorizeParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }

export function useAuthorize<TData = Awaited<ReturnType<typeof authorize>>, TError = Authorize500>(
 params: AuthorizeParams, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof authorize>>, TError, TData>>, }

  ):  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getAuthorizeQueryOptions(params,options)

  const query = useQuery(queryOptions) as  UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = queryOptions.queryKey ;

  return query;
}



