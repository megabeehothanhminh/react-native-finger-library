import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-finger-library' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const BleFingerModule = NativeModules.BleFingerModule
  ? NativeModules.BleFingerModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function init() {
  return new Promise<void>(function (resolve, reject) {
    return BleFingerModule.init(
      function () {
        return resolve();
      },
      function (error: any) {
        return reject(error);
      }
    );
  });
}
export function getDeviceList() {
  return new Promise(function (resolve, reject) {
    return BleFingerModule.getDeviceList(
      function (finger: unknown) {
        console.log(finger);

        return resolve(finger);
      },
      function (error: any) {
        return reject(error);
      }
    );
  });
}

export function selectedDevice(deviceId: string) {
  return new Promise(function () {
    return BleFingerModule.connectPrinter(deviceId);
  });
}

export function registerFinger() {
  return new Promise(function () {
    return BleFingerModule.enrolTemplate();
  });
}

export function matchTemplate(mRef: string) {
  return new Promise(function () {
    return BleFingerModule.matchTemplate(mRef);
  });
}

export function cleanFolder() {
  return new Promise(function () {
    return BleFingerModule.cleanFolder();
  });
}
