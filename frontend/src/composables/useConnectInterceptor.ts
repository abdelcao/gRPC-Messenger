import TokenService from "@/utils/TokenService";
import type { Interceptor } from "@connectrpc/connect";

export function useConnectInterceptor(): Interceptor {
  return (next) => async (req) => {
    const token = TokenService.getAccessToken()
    if (token) {
      req.header.set("Authorization", `Bearer ${token}`);
    }
    return next(req);
  };
}
