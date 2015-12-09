CS 143 Network Project

<b>Input Format</b>:
Four numbers, H, R, L, F which correspond to the number of hosts, routers, links, and flows respectively

H lines, each containing the name of a host

R lines, each containing the name of a router

L lines, each containing the name of a link, left endpoint, right endpoint, link rate, link delay, and link buffer size

F lines, each containing the name of a flow, flow src, flow dest, data amount, and float start time

F lines, each containing the name of a flow and one of {RENO, FAST}, the preferred TCP congestion control algorithm
