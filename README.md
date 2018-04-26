# Edge detection operators
This repository contains the code neccessary to run the Mode Cardinality (MC) and Spatially-Informed Mode Cardinality (SI-MC) edge detection operators proposed in the journal article:
>Horrocks, T., Holden, E.-J., Wedge, D., Wijns, C. (2018), "A nonparametric boundary detection technique applied to 3D inverted surveys of the Kevitsa Ni-Cu-PGE deposit", Geophysics, 83, 1, IM:1–13.

# How to use
To get started, run _build.bat_.

# Usage
The commands are listed below. The _load_ commands expects a CSV file with columns corresponding to x, y, z, and v(alue). The points must be in a regular grid (corresponding to voxel centres). If the grid is rotated in the x,y-plane, the rotation option can be used.

```
set-option
    Required (at least one of the following)
        --minimal-export "true/false" (default: "false")
        --attribute-bandwidth-selector "silverman/5.0"
        --projection-bandwidth-selector "silverman/5.0"
        --local-radius "15.0" or "15.0,20.0,30.0"
        --bandwidth-multiplier "1.0"

load 
    Required
        --load-path "path" 
    Optional
        --log-transform "true/false" (default: "false")
        --export-path "path"
        --rotation 0 (in degrees)

bandwidth-volume
    Required
        --attribute-export-path "path"
    Optional
        --projection-export-path "path"

mode-volume
    Required
        --export-path "path"
        --number-of-subdivisions "100"
        --use-gradient "true"
        --gradient-cutoff "0.05" (not required if use-gradient is "false")
```
