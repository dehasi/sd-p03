/**
 * fib.js
 *
 * @param {array} args
 * @return {string}
 */

const fib = (n) => {
    if(n < 2) return 1
    else return fib(n-1) + fib(n-2)
}
const result = fib(args[0])

String(result)
