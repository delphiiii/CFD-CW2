import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.interpolate import RectBivariateSpline

starccm_rho=1.18415

# def calculate_dns_mfr(datas, rho):
#     mfrs = {}
#     for label, data in datas.items():
#         u = data['Velocity[i] (m/s)'].to_numpy()
#         x = data['Y (m)'].unique()
#         y = data['Z (m)'].unique()
#         interp = RectBivariateSpline(x, y, u.reshape(x.size, y.size))
#         mfr = interp.integral(0, 1, 0, 1) * rho
#         mfrs[label] = mfr * 4
#     return mfrs

Res = [20,200,2000]
grids = [50,100,200]

laminar_data = {}
mfr_data = {}

for Re in Res:
    for grid in grids:
        df = pd.read_csv(f'Laminar/laminar_Re{Re}_{grid}cell.csv')
        df['Z (m)'] = round(df['Z (m)'],5)
        df = df.sort_values(by=['Y (m)', 'Z (m)'])
        laminar_data[f"Re{Re}_{grid}cell"] = df
        # print(f"Re{Re}_{grid}cell")
        u = df['Velocity[i] (m/s)'].to_numpy()
        x = df['Y (m)'].unique()
        y = df['Z (m)'].unique()
        print(x,y)
        interp = RectBivariateSpline(x, y, u.reshape(x.size, x.size))
        mfr = interp.integral(0, 1, 0, 1) * starccm_rho
        mfr_data[f"Re{Re}_{grid}cell"] = mfr * 4

# print(data['Re2000_50cell'])
print(mfr_data.items())

def calculate_GCI(f1, f2, f3):
    # f1, f2, f3 = sorted([f1, f2, f3])
    r = 2
    p = np.log(abs(f3 - f2) / abs(f2 - f1)) / np.log(r)
    Fs = 1.25
    GCI_12 = Fs * np.abs((f1 - f2) / f1) / (r ** p - 1)
    GCI_23 = Fs * np.abs((f2 - f3) / f2) / (r ** p - 1)
    return GCI_12, GCI_23, GCI_23 / (r ** p * GCI_12)

print(calculate_GCI(mfr_data['Re20_50cell'],mfr_data['Re20_100cell'],mfr_data['Re20_200cell']))
print(calculate_GCI(mfr_data['Re200_50cell'],mfr_data['Re200_100cell'],mfr_data['Re200_200cell']))
print(calculate_GCI(mfr_data['Re2000_50cell'],mfr_data['Re2000_100cell'],mfr_data['Re2000_200cell']))
# print([mfr_data['Re20_50cell'],mfr_data['Re20_100cell'],mfr_data['Re20_200cell']].sort())
