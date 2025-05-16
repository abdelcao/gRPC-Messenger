export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  delay: number,
): (...args: Parameters<T>) => void {
  let lastCall = 0
  return function (...args: Parameters<T>) {
    const now = new Date().getTime()
    if (now - lastCall >= delay) {
      lastCall = now
      fn(...args)
    }
  }
}

export async function countAsyncIterable<T>(
  iterable: AsyncIterable<T> | undefined,
): Promise<number> {
  let count = 0
  if (!iterable) return count
  for await (const _ of iterable) {
    count++
  }
  return count
}
