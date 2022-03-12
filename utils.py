import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from scipy.interpolate import griddata
from scipy.interpolate import RectBivariateSpline
from metpy.calc import vorticity
from metpy.units import units
import xarray


class Data:
    def __init__(self, Re_numbers, models, grids, rho=1.18415, mu=1.85508E-5):
        self.Re_numbers = Re_numbers
        self.models = models
        self.grids = grids
        self.rho = rho
        self.mu = mu
        self.nu = mu / rho
        self.data, self.mfr_plot, self.residuals_plot, self.mfr = read_star_ccm(Re_numbers, grids, models)
        self.dns = read_yz_sections(Re_numbers)
        self.dns_mfr = calculate_dns_mfr(self.dns, rho, self.nu, self.Re_numbers)
        self.calculate_ke_and_vorticity(self.Re_numbers)
        if len(self.grids) == 3:
            self.convergence_index = self.calculate_convergence_index()

    def plot_mfr(self, model, Re=300, grid=200):
        fig, ax = plt.subplots(figsize=(12, 8))
        data = self.mfr_plot[model][Re][grid] * 4
        data.plot(ax=ax, legend=False)
        ax.set_ylabel('Mass Flow Rate (kg/s)', fontsize=18)
        ax.set_xlabel('Iteration', fontsize=18)
        ax.grid()

    def calculate_ke_and_vorticity(self, Re_numbers):
        for Re in Re_numbers:
            u_tau = Re * self.nu
            data = self.dns[Re]
            ke = 0.5 * (data["<u'u'>"] + data["<v'v'>"] + data["<w'w'>"]) * u_tau**2

            v, w = data['<v>'].values * u_tau, data['<w>'].values * u_tau
            y, z = data['y'].unique(), data['z'].unique()
            v_mesh, w_mesh = v.reshape(y.size, z.size), w.reshape(y.size, z.size)
            v_xarray = xarray.DataArray(v_mesh, attrs={'units': 'm/s'})
            w_xarray = xarray.DataArray(w_mesh, attrs={'units': 'm/s'})
            vort = vorticity(v_xarray, w_xarray,
                             dx=units.Quantity(np.diff(y, n=1), 'm'),
                             dy=units.Quantity(np.diff(z, n=1), 'm'))
            vort = vort.values.reshape(vort.size)
            df = pd.DataFrame(
                np.vstack([ke, vort]).T,
                columns=['turbulent_ke', 'vorticity']).reset_index().drop('index', axis=1)
            self.dns[Re] = pd.concat([self.dns[Re], df], axis=1)

    def calculate_convergence_index(self):
        conv = {}
        for model in self.models:
            conv[model] = {}
            for re in self.Re_numbers:
                mfr_data = self.mfr[model][re]
                conv[model][re] = calculate_GCI(mfr_data)
        return pd.DataFrame(conv)

    def plot_velocity_profile(self, model, Re=300, grid=200, quiver=True):
        fig, axes = plt.subplots(1, 2, figsize=(18, 6), sharey=True)
        if model not in self.models:
            raise ValueError('Invalid model. Must be one of:', self.models)
        dns = self.dns[Re]
        star = self.data[model][Re][grid]
        star, dns = down_sample_grid(star, dns)
        im = None
        for idx, a in enumerate(axes.flat):
            if idx == 0:
                u = self.nu * Re * dns['<u>']
                levels = np.linspace(u.min(), u.max(), 20)
                im = axes[0].tricontourf(dns['y'], dns['z'], u, levels=levels)
                axes[0].set_ylabel('z (m)', fontsize=18)
                axes[0].set_xlabel('y (m)', fontsize=18)
                if quiver:
                    axes[0].quiver(dns['y'], dns['z'], dns['<v>'] * self.nu * Re, dns['<w>'] * self.nu * Re)
            else:
                y = star['Y (m)']
                z = star['Z (m)']
                u = star['Velocity[i] (m/s)']
                levels = np.linspace(u.min(), u.max(), 20)
                im = axes[1].tricontourf(y, z, u, levels=levels)
                axes[1].set_ylabel('z (m)', fontsize=18)
                axes[1].set_xlabel('y (m)', fontsize=18)
                if quiver:
                    axes[1].quiver(star['Y (m)'],
                                   star['Z (m)'],
                                   star['Velocity[j] (m/s)'],
                                   star['Velocity[k] (m/s)'])

        fig.colorbar(im, ax=axes.ravel().tolist())
        plt.show()

    def plot_turbulent_ke(self, model, Re=300, grid=200):
        fig, axes = plt.subplots(1, 2, figsize=(18, 6), sharey=True)
        if model not in self.models:
            raise ValueError('Invalid model. Must be one of:', self.models)
        dns = self.dns[Re]
        star = self.data[model][Re][grid]
        star, dns = down_sample_grid(star, dns)
        im = None
        for idx, a in enumerate(axes.flat):
            if idx == 0:
                turbulent_ke = dns['turbulent_ke']
                levels = np.linspace(turbulent_ke.min(), turbulent_ke.max(), 20)
                im = axes[0].tricontourf(dns['y'], dns['z'], turbulent_ke, levels=levels)
                axes[0].set_ylabel('z (m)', fontsize=18)
                axes[0].set_xlabel('y (m)', fontsize=18)
                dns = dns.iloc[::2]
            else:
                y = star['Y (m)']
                z = star['Z (m)']
                turbulent_ke = star['Turbulent Kinetic Energy (J/kg)']
                levels = np.linspace(turbulent_ke.min(), turbulent_ke.max(), 20)
                im = axes[1].tricontourf(y, z, turbulent_ke, levels=levels)
                axes[1].set_ylabel('z (m)', fontsize=18)
                axes[1].set_xlabel('y (m)', fontsize=18)

        fig.colorbar(im, ax=axes.ravel().tolist())
        plt.show()

    def plot_vorticity(self, model, Re=300, grid=200):
        fig, axes = plt.subplots(1, 2, figsize=(18, 6), sharey=True)
        if model not in self.models:
            raise ValueError('Invalid model. Must be one of:', self.models)
        dns = self.dns[Re]
        star = self.data[model][Re][grid]
        star, dns = down_sample_grid(star, dns)
        im = None
        for idx, a in enumerate(axes.flat):
            if idx == 0:
                vort = dns['vorticity']
                im = axes[0].tricontourf(dns['y'], dns['z'], vort)
                axes[0].set_ylabel('z (m)', fontsize=18)
                axes[0].set_xlabel('y (m)', fontsize=18)
                dns = dns.iloc[::2]
            else:
                y = star['Y (m)']
                z = star['Z (m)']
                vort = star['Vorticity[i] (/s)']
                im = axes[1].tricontourf(y, z, vort)
                axes[1].set_ylabel('z (m)', fontsize=18)
                axes[1].set_xlabel('y (m)', fontsize=18)

        fig.colorbar(im, ax=axes.ravel().tolist())
        plt.show()

    def plot_velocity_errors(self, model, Re=300, grid=200, absolute=True):
        dns = self.dns[Re]
        star = self.data[model][Re][grid]
        star, dns = down_sample_grid(star, dns)
        errors = star['Velocity[i] (m/s)'] - dns['<u>'] * Re * self.nu
        if absolute:
            errors = np.abs(errors)
        plt.figure(figsize=(12, 8))
        plt.tricontourf(dns['y'], dns['z'], errors)
        plt.ylabel('Y (m)', fontsize=18)
        plt.xlabel('Z (m)', fontsize=18)
        plt.colorbar()

    def plot_vorticity_errors(self, model, Re=300, grid=200, absolute=True):
        dns = self.dns[Re]
        star = self.data[model][Re][grid]
        star, dns = down_sample_grid(star, dns)
        errors = star['Vorticity[i] (/s)'] - dns['vorticity']
        if absolute:
            errors = np.abs(errors)
        plt.figure(figsize=(12, 8))
        plt.tricontourf(dns['y'], dns['z'], errors)
        plt.ylabel('Y (m)', fontsize=18)
        plt.xlabel('Z (m)', fontsize=18)
        plt.colorbar()

    def plot_turbulent_ke_errors(self, model, Re=300, grid=200, absolute=True):
        dns = self.dns[Re]
        star = self.data[model][Re][grid]
        star, dns = down_sample_grid(star, dns)
        errors = star['Turbulent Kinetic Energy (J/kg)'] - dns['turbulent_ke']
        if absolute:
            errors = np.abs(errors)
        plt.figure(figsize=(12, 8))
        plt.tricontourf(dns['y'], dns['z'], errors)
        plt.ylabel('Y (m)', fontsize=18)
        plt.xlabel('Z (m)', fontsize=18)
        plt.colorbar()


def down_sample_grid(star, dns):
    if len(star) > len(dns):
        data = star.copy()
        coordinates = dns[['y', 'z']].values
        down_sampled = griddata(data[['Y (m)', 'Z (m)']].values,
                                data.drop(['Y (m)', 'Z (m)'], axis=1),
                                coordinates,
                                method='nearest')
        down_sampled = pd.DataFrame(np.concatenate((down_sampled, coordinates), axis=1))
        down_sampled.columns = data.columns
        return down_sampled.reset_index(), dns.reset_index()

    else:
        data = dns.copy()
        coordinates = star[['Y (m)', 'Z (m)']].values
        down_sampled = griddata(data[['y', 'z']].values,
                                data.drop(['y', 'z'], axis=1),
                                coordinates,
                                method='nearest')
        down_sampled = pd.DataFrame(np.concatenate((coordinates, down_sampled), axis=1))
        down_sampled.columns = data.columns
        return star.reset_index(), down_sampled.reset_index()


def read_yz_sections(Re_numbers):
    dns_data = {}
    for Re in Re_numbers:
        fpath = f'DuctFlow/YZsections/Re{Re}/DuctFlow_Re{Re}_YZ.dat'
        df = pd.read_csv(fpath, delimiter=' ', low_memory=False).reset_index()
        df.columns = ['y'] + list(df.iloc[0][1:])
        df = df.drop([0, 1], axis=0).reset_index(drop=True).astype('float')
        df = df[(df['y'] <= 0.5) & (df['z'] <= 0.5)]
        dns_data[Re] = df.reset_index().drop('index', axis=1)

    return dns_data


def read_star_ccm(Re, grids, models):
    data, mfrs, residuals = {}, {}, {}
    mfr = {}
    for model in models:
        data[model], mfrs[model], residuals[model], mfr[model] = {}, {}, {}, {}
        for re in Re:
            data[model][re], mfrs[model][re], residuals[model][re], mfr[model][re] = {}, {}, {}, {}
            for grid in grids:
                data_df = pd.read_csv(os.path.join('Outputs', model, f'Re{re}', 'data', f'data_{grid}.csv'))
                mfr_df = pd.read_csv(
                    os.path.join('Outputs', model, f'Re{re}', 'mfr', f'mfr_plot_{grid}.csv'), index_col=0)
                residuals_df = pd.read_csv(
                    os.path.join('Outputs', model, f'Re{re}', 'residuals', f'residuals_{grid}.csv'), index_col=0)

                data[model][re][grid], mfrs[model][re][grid], residuals[model][re][grid] = data_df, mfr_df, residuals_df
                mfr[model][re][grid] = mfr_df.iloc[-1].values[0] * 4
    return data, mfrs, residuals, mfr


def calculate_dns_mfr(data, rho, nu, Re_numbers):
    mfrs = {}
    for Re in Re_numbers:
        dns_result = data[Re]
        u_tau = Re * nu
        dns_u = dns_result['<u>'].to_numpy() * u_tau
        x = dns_result['y'].unique()
        y = dns_result['z'].unique()
        dns_interp = RectBivariateSpline(dns_result['y'].unique(), dns_result['z'].unique(),
                                         dns_u.reshape(x.size, y.size))
        dns_mfr = dns_interp.integral(0, 1, 0, 1) * rho
        mfrs[Re] = dns_mfr * 4
    return mfrs


def calculate_GCI(mfrs):
    f1, f2, f3 = mfrs.values()
    r = 2
    p = np.log((f3 - f2) / (f2 - f1)) / np.log(r)
    Fs = 1.25
    GCI_12 = Fs * np.abs((f1 - f2) / f1) / (r ** p - 1)
    GCI_23 = Fs * np.abs((f2 - f3) / f2) / (r ** p - 1)
    return GCI_23 / (r ** p * GCI_12)
