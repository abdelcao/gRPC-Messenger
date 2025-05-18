import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import relativeTime from 'dayjs/plugin/relativeTime'

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

/**
 * Count time from a date
 */

dayjs.extend(relativeTime)
dayjs.extend(utc)

/**
 *
 * @param date : 'yyyy-mm-ddThh:mm:ss'
 * @returns
 */
export function timeAgo(mydate: string | Date): string {
  const target = dayjs.utc(mydate)
  const now = dayjs.utc()
  return target.fromNow()
}
