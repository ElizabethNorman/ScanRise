# ScanRise

ScanRise is a simple Android alarm app. The idea is to make it harder to turn off an alarm half-asleep and immediately go back to bed. 

You can attach a barcode from something in another room — coffee, toothpaste, cereal, whatever works — and ScanRise will keep the alarm going until you physically get up and scan it.

This is an early beta build, so expect a little roughness around the edges. **To accommodate this there is still a "stop alarm immediately" button. I will replace that with a much more elaborate failsafe after a few friends let me know how things go!!!**

## Very important beta limitation!!!!!!
I cannot for the life of me get it to reset alarms after a restart. This is because Android deliberately restricts background app startup, so restoring the alarms requires special handling that I'm still
figuring out :)

After completely restarting your phone, **open ScanRise once before relying on your alarms**.

Android/OEM reboot behaviour is still being worked on, and on some phones ScanRise may not restore its scheduled alarms until the app has been opened after a reboot.

For a beta build, assume:

> Reboot phone → open ScanRise once → alarms are good to go.

If an alarm is important, it is also worth doing a test alarm on your particular phone before depending on it.

## What it does

- Create one-time or repeating alarms.
- Choose which days of the week an alarm should run.
- Scan and save real-world barcodes as objects.
- Attach a barcode to an alarm.
- When the alarm goes off, scan the assigned barcode to dismiss it.
- Alarms use Android's exact alarm system so they can fire at the intended time.

## Before installing

This build is being shared directly rather than through Google Play, so Android will treat it as an app from an unknown source.

You may see warnings while installing it. That is expected for a sideloaded APK.

You will also be asked for a few Android permissions when you use ScanRise:

- **Camera** — needed to scan barcodes.
- **Notifications** — needed for alarm notifications. **You may need to manually open the AppInfo page to manually give it notification permissions**
- **Exact alarms / alarms & reminders** — needed so alarms can fire at the time you set.
- ScanRise may also ask Android for the permissions/settings needed to display alarms prominently.

## How to sideload ScanRise

1. Download the `ScanRise-...apk` file onto your Android phone.
2. Open the APK from your browser, Downloads app, Files app, or wherever you saved it.
3. Android may say that the app you are using is not allowed to install unknown apps.
4. Tap **Settings** when prompted.
5. Enable **Allow from this source** for the browser or file manager you are using.
6. Go back and open the APK again.
7. Tap **Install**.
8. Once installation finishes, open **ScanRise**.

The wording varies slightly between Android manufacturers, but the general process is the same.

You can turn **Allow from this source** back off afterward if you want.

## First-time setup

When you first open ScanRise:

1. Approve the requested permissions.
2. If Android asks you to allow **Alarms & reminders** or exact alarms, enable it.
3. Open the **Objects** section and add something with a barcode.
4. Give the object a name and save it.
5. Create an alarm and attach that object.
6. Set the alarm a few minutes ahead the first time so you can test it without creating tomorrow morning's problem tonight.

When the alarm fires, ScanRise should open the scanner. Scan the barcode you assigned to that alarm to dismiss it.

## Updating to a newer beta

If I send you a newer ScanRise APK, you should normally be able to install it directly over the existing version.

You do **not** need to uninstall the old version first.

Installing the update over the existing app should preserve your saved alarms and barcodes.

## If something breaks

This is a beta, so weird behaviour is useful information.

If an alarm fails, the scanner behaves strangely, or Android does something unexpected, send me:

- Your phone model.
- Your Android version.
- What you expected to happen.
- What actually happened.
- Whether the phone had recently been restarted.
- A screenshot if one is useful.

Also: if you ever find yourself having to scan the correct barcode **twice** before the alarm stops, tell me. That's a bug I'm specifically watching for.

## Uninstalling

ScanRise can be removed like any other Android app:

**Settings → Apps → ScanRise → Uninstall**

Uninstalling it will remove its locally saved alarms and barcode objects.

---
## Attributions
I took the logo from [FlatIcon](https://www.flaticon.com/free-icon/sunset_4814444?term=sunrise&page=1&position=12&origin=search&related_id=4814444). Thank you!

Thanks for testing my slightly hostile alarm clock.
