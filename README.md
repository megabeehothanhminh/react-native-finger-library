# react-native-finger-library

finger-sdk

## Installation

```sh
npm install react-native-finger-library
```

## Usage

```js
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

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
