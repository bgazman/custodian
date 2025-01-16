/**
 * Generated by orval v7.4.1 🍺
 * Do not edit manually.
 * Your API
 * OpenAPI spec version: 1.0.0
 */
import {
  useMutation
} from '@tanstack/react-query'
import type {
  MutationFunction,
  UseMutationOptions,
  UseMutationResult
} from '@tanstack/react-query'
import type {
  InitiatePasswordReset200,
  InitiatePasswordReset500,
  InitiatePasswordResetBody,
  ResetPassword200,
  ResetPassword500,
  ResetPasswordRequest
} from '../index.schemas'
import { customFetcher } from '../../common/SecureApiClient';



export const resetPassword = (
    resetPasswordRequest: ResetPasswordRequest,
 signal?: AbortSignal
) => {
      
      
      return customFetcher<ResetPassword200>(
      {url: `http://localhost:8080/forgot-password/reset`, method: 'POST',
      headers: {'Content-Type': 'application/json', },
      data: resetPasswordRequest, signal
    },
      );
    }
  


export const getResetPasswordMutationOptions = <TData = Awaited<ReturnType<typeof resetPassword>>, TError = ResetPassword500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: ResetPasswordRequest}, TContext>, }
) => {
const mutationKey = ['resetPassword'];
const {mutation: mutationOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof resetPassword>>, {data: ResetPasswordRequest}> = (props) => {
          const {data} = props ?? {};

          return  resetPassword(data,)
        }

        


  return  { mutationFn, ...mutationOptions } as UseMutationOptions<TData, TError,{data: ResetPasswordRequest}, TContext>}

    export type ResetPasswordMutationResult = NonNullable<Awaited<ReturnType<typeof resetPassword>>>
    export type ResetPasswordMutationBody = ResetPasswordRequest
    export type ResetPasswordMutationError = ResetPassword500

    export const useResetPassword = <TData = Awaited<ReturnType<typeof resetPassword>>, TError = ResetPassword500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: ResetPasswordRequest}, TContext>, }
): UseMutationResult<
        TData,
        TError,
        {data: ResetPasswordRequest},
        TContext
      > => {

      const mutationOptions = getResetPasswordMutationOptions(options);

      return useMutation(mutationOptions);
    }
    export const initiatePasswordReset = (
    initiatePasswordResetBody: InitiatePasswordResetBody,
 signal?: AbortSignal
) => {
      
      
      return customFetcher<InitiatePasswordReset200>(
      {url: `http://localhost:8080/forgot-password/initiate`, method: 'POST',
      headers: {'Content-Type': 'application/json', },
      data: initiatePasswordResetBody, signal
    },
      );
    }
  


export const getInitiatePasswordResetMutationOptions = <TData = Awaited<ReturnType<typeof initiatePasswordReset>>, TError = InitiatePasswordReset500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: InitiatePasswordResetBody}, TContext>, }
) => {
const mutationKey = ['initiatePasswordReset'];
const {mutation: mutationOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof initiatePasswordReset>>, {data: InitiatePasswordResetBody}> = (props) => {
          const {data} = props ?? {};

          return  initiatePasswordReset(data,)
        }

        


  return  { mutationFn, ...mutationOptions } as UseMutationOptions<TData, TError,{data: InitiatePasswordResetBody}, TContext>}

    export type InitiatePasswordResetMutationResult = NonNullable<Awaited<ReturnType<typeof initiatePasswordReset>>>
    export type InitiatePasswordResetMutationBody = InitiatePasswordResetBody
    export type InitiatePasswordResetMutationError = InitiatePasswordReset500

    export const useInitiatePasswordReset = <TData = Awaited<ReturnType<typeof initiatePasswordReset>>, TError = InitiatePasswordReset500,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<TData, TError,{data: InitiatePasswordResetBody}, TContext>, }
): UseMutationResult<
        TData,
        TError,
        {data: InitiatePasswordResetBody},
        TContext
      > => {

      const mutationOptions = getInitiatePasswordResetMutationOptions(options);

      return useMutation(mutationOptions);
    }
    