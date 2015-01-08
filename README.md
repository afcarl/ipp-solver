Integer Polynomial Program Solver
=================================

A solver for integer polynomial programming problems.
An Integer Polynomial Programming problem (IPP) is defined as follows -

```
Maximize    f(x1, x2, ..., xn)
Subject to  g1(x1, x2, ..., xn) <= 0
            g2(x1, x2, ..., xn) <= 0
            ...
            gm(x1, x2, ..., xn) <= 0
            x1, x2, ..., xn integer
```

where each `xi` takes finite integer values, and the objective function `f(x1,x2,...,xn)`, and each of the constraints `gj(x1,x2,...,xn)` are polynomials on `x1,x2,...,xn`

********************************************************************************

Pre-Release Version 0.1-alpha
-----------------------------

Currently able to solve only Linearly constrained Integer Multi-Linear Programming problems (i.e.- problems whose objective function is a Multi-Linear expression and all the constraints are linear constraints)
