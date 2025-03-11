import numpy as np
import scipy as sp
import scipy.stats as s




#---------------------------------------------------------
#HCA-LBSA vs. ELBSA
data = [1,0,-2,2,3,4,3,3,1,1]
n = len(data)
print(n)
r, p = s.wilcoxon(data)
print('HCA-LBSA vs. ELBSA: R+=', n*(n+1)/2-r, 'R-=', r, 'p-value=',format(p,'.2e'))

#HCA-LBSA vs. ELBSA tanpa kasus terburuk
data = [0.061,-0.026,0.008,-0.041,0.022,0.079,-0.045,-0.003,0.024,-0.007,
        -0.028,-0.012,-0.032,-0.027,-0.098,0.009,0.031,0.071,0.046,0.015,-0.004,-0.068,0.063,0.160]
n = len(data)
print(n)
r, p = s.wilcoxon(data)
print('HCA-LBSA vs. ELBSA 2.0: R+=', n*(n+1)/2-r, 'R-=', r, 'p-value=',format(p,'.2e'))

#HCA-LBSA vs. LBSA 
data = [0.292,0.084,0.054,0.129,0.138,0.308,0.091,0.114,0.101,-2.022,-0.065,0.085,0.059,
        0.052,0.206,0.157,0.025,0.267,-0.214,0.250,0.193,-2.146,0.462,0.494,0.662,      0.324]
n = len(data)
print(n)
r, p = s.wilcoxon(data)
print('HCA-LBSA vs. LBSA: R+=', n*(n+1)/2-r, 'R-=', r, 'p-value=',format(p,'.2e'))

#HCA-LBSA vs. ASA-GS
data = [1.398,1.228,2.357,0.843,0.897,2.798,1.738,0.500,1.988,1.862,2.569,2.782]
n = len(data)
print(n)
r, p = s.wilcoxon(data)
print('HCA-LBSA vs. ASA-GS: R+=', n*(n+1)/2-r, 'R-=', r, 'p-value=',format(p,'.2e'))

#HCA-LBSA vs. SOS-SA
data = [0.448,0.788,0.467,0.203,-1.833,2.728,-0.392,-0.100,0.878,0.842,0.919,1.212]
n = len(data)
print(n)
r, p = s.wilcoxon(data)
print('HCA-LBSA vs. SOS-SA: R+=', n*(n+1)/2-r, 'R-=', r, 'p-value=',format(p,'.2e'))


#HCA-LBSA vs. AHSA-TS
data = [0.386,0.247,0.587,0.946,0.314,-0.593,0.948,0.862,0.723,1.154,1.032,-0.075,0.802,0.501,0.340,1.004,1.794]
n = len(data)
print(n)
r, p = s.wilcoxon(data)
print('HCA-LBSA vs. AHSA-TS: R+=', n*(n+1)/2-r, 'R-=', r, 'p-value=',format(p,'.2e'))

#HCA-LBSA vs. D-CLPSO
data = [0.148,0.335,0.152,0.191,0.053,-2.025,-0.020,0.357,0.236,0.102,0.242,-0.175,0.365,0.416,0.434,0.294,0.319]
n = len(data)
print(n)
r, p = s.wilcoxon(data)
print('HCA-LBSA vs. D-CLPSO: R+=', n*(n+1)/2-r, 'R-=', r, 'p-value=',format(p,'.2e'))





