# Allocation tracking records
https://developer.android.com/studio/profile/allocation-tracker-walkthru.html

Allocation tracking records app memory allocations and lists all allocations for the profiling cycle, including the call stack, size, and allocating code. It helps you to:

Identify where many similar object types, from roughly the same call stack, are allocated and deallocated over a very short period of time.
Find the places in your code that may contribute to inefficient memory use.

The Allocation Tracker is useful when you want to get a sense of what kinds of allocation are happening over a given time period, 
but it doesn't give you any information about the overall state of your application's heap.

