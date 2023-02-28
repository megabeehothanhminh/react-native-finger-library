
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNFingerLibrarySpec.h"

@interface FingerLibrary : NSObject <NativeFingerLibrarySpec>
#else
#import <React/RCTBridgeModule.h>

@interface FingerLibrary : NSObject <RCTBridgeModule>
#endif

@end
