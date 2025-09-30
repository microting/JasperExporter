# JasperReports 6.19.1 → 7.0.3 Migration Guide

## Migration Assessment Summary

This document provides a comprehensive assessment of migrating from JasperReports 6.19.1 to 7.0.3 for the JasperExporter project.

**Migration Status**: ✅ Code Changes Complete | ⚠️ Test Validation Pending
**Risk Level**: MEDIUM (Breaking API changes, requires code modifications)
**Estimated Effort**: 8-16 hours for complete implementation and testing

---

## Table of Contents

1. [Breaking Changes](#breaking-changes)
2. [Impacted Classes and Methods](#impacted-classes-and-methods)
3. [Code Changes Required](#code-changes-required)
4. [Build and Toolchain Requirements](#build-and-toolchain-requirements)
5. [Migration Plan](#migration-plan)
6. [Testing Strategy](#testing-strategy)
7. [Known Issues](#known-issues)
8. [Rollback Plan](#rollback-plan)

---

## Breaking Changes

### 1. Exporter Modules Separated (CRITICAL)

**Impact**: HIGH - Requires code changes and new dependencies

JasperReports 7.0 has separated exporters into independent modules to reduce the core library footprint and dependencies.

#### PDF Exporter
- **Old Module**: Included in `jasperreports` core
- **New Module**: `net.sf.jasperreports:jasperreports-pdf:7.0.3`
- **Package Change**: 
  - From: `net.sf.jasperreports.engine.export.JRPdfExporter`
  - To: `net.sf.jasperreports.pdf.JRPdfExporter`
- **Configuration Change**:
  - From: `net.sf.jasperreports.export.SimplePdfReportConfiguration`
  - To: `net.sf.jasperreports.pdf.SimplePdfReportConfiguration`

#### Excel Exporter (XLS/XLSX)
- **Old Module**: Included in `jasperreports` core
- **New Module**: `net.sf.jasperreports:jasperreports-excel-poi:7.0.3`
- **Package Change**:
  - From: `net.sf.jasperreports.engine.export.JRXlsExporter`
  - To: `net.sf.jasperreports.poi.export.JRXlsExporter`
- **Configuration**: Remains in core module (`net.sf.jasperreports.export.SimpleXlsReportConfiguration`)

#### Other Exporters (Unchanged)
The following exporters remain in the core module:
- RTF: `net.sf.jasperreports.engine.export.JRRtfExporter`
- ODT: `net.sf.jasperreports.engine.export.oasis.JROdtExporter`
- DOCX: `net.sf.jasperreports.engine.export.ooxml.JRDocxExporter`
- PPTX: `net.sf.jasperreports.engine.export.ooxml.JRPptxExporter`
- XLSX: `net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter`

### 2. Deprecated Reflection API Removed

**Impact**: MEDIUM - Code compilation failure

**Issue**: `Class.newInstance()` has been deprecated since Java 9 and removed in Java 16+

**Change Required**:
```java
// Old (deprecated):
return exporterClass.newInstance();

// New (required):
return exporterClass.getDeclaredConstructor().newInstance();
```

**Exception Handling Update**:
```java
// Old:
catch (IllegalAccessException | InstantiationException e)

// New:
catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
```

### 3. Dependency Updates

JasperReports 7.0.3 includes updated transitive dependencies:
- Jackson: Updated to 2.18.2 (from older version)
- Batik: Updated to 1.18 (from older version)
- Apache Commons: Various updates
- Better Java 17+ compatibility

---

## Impacted Classes and Methods

### Detailed Impact Analysis

#### 1. `service/src/main/java/com/microting/report/jasper/convertion/JasperReportConvertor.java`

**Priority**: CRITICAL - Required for compilation

**Changes Made**:
1. **Import Statements** (Lines 19-20, 37):
   ```java
   // Changed:
   import net.sf.jasperreports.engine.export.JRPdfExporter;
   import net.sf.jasperreports.engine.export.JRXlsExporter;
   import net.sf.jasperreports.export.SimplePdfReportConfiguration;
   
   // To:
   import net.sf.jasperreports.pdf.JRPdfExporter;
   import net.sf.jasperreports.poi.export.JRXlsExporter;
   import net.sf.jasperreports.pdf.SimplePdfReportConfiguration;
   ```

2. **Reflection API** (Line 113):
   ```java
   // Changed:
   return v.exporterClass.newInstance();
   
   // To:
   return v.exporterClass.getDeclaredConstructor().newInstance();
   ```

3. **Exception Handling** (Line 114):
   ```java
   // Changed:
   catch (IllegalAccessException | InstantiationException e)
   
   // To:
   catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
   ```

**Risk Assessment**: LOW
- Changes are straightforward and well-defined
- No logic changes required
- Exception handling is defensive

#### 2. `service/src/test/java/com/microting/report/jasper/ReportWithOverflowTextAndBrokenImagesTest.java`

**Priority**: HIGH - Required for test compilation

**Changes Made**:
1. **Import Statement** (Line 11):
   ```java
   // Changed:
   import net.sf.jasperreports.engine.export.JRPdfExporter;
   
   // To:
   import net.sf.jasperreports.pdf.JRPdfExporter;
   ```

**Risk Assessment**: LOW
- Simple import change
- No logic modifications needed

#### 3. `service/src/main/java/com/microting/report/jasper/JasperExporterEngine.java`

**Priority**: LOW - No changes required

**Status**: ✅ Compatible
- Uses stable JasperReports APIs
- No deprecated method usage
- All imports remain valid

#### 4. `build.gradle`

**Priority**: CRITICAL - Required for dependency resolution

**Changes Made**:
```gradle
// Added new dependencies:
dependency 'net.sf.jasperreports:jasperreports-pdf:7.0.3'
dependency 'net.sf.jasperreports:jasperreports-excel-poi:7.0.3'

// Updated version:
dependency 'net.sf.jasperreports:jasperreports:6.19.1' → '7.0.3'
```

#### 5. `service/build.gradle`

**Priority**: CRITICAL - Required for module dependencies

**Changes Made**:
```gradle
dependencies {
    implementation project(":fonts"),
            'net.sf.jasperreports:jasperreports',
            'net.sf.jasperreports:jasperreports-pdf',        // NEW
            'net.sf.jasperreports:jasperreports-excel-poi',  // NEW
            ...
}
```

---

## Code Changes Required

### Summary of Changes

| File | Lines Changed | Type | Priority |
|------|--------------|------|----------|
| `build.gradle` | 2 added | Dependencies | CRITICAL |
| `service/build.gradle` | 2 added | Dependencies | CRITICAL |
| `JasperReportConvertor.java` | 3 imports, 2 code | API Update | CRITICAL |
| `ReportWithOverflowTextAndBrokenImagesTest.java` | 1 import | API Update | HIGH |
| **Total** | **10 lines** | | |

### Detailed Changes

#### 1. Root `build.gradle`
**Location**: `/build.gradle` lines 46-48

```gradle
dependencies {
    dependency 'net.sf.jasperreports:jasperreports:7.0.3'          // Changed from 6.19.1
    dependency 'net.sf.jasperreports:jasperreports-pdf:7.0.3'      // NEW
    dependency 'net.sf.jasperreports:jasperreports-excel-poi:7.0.3' // NEW
}
```

#### 2. Service `build.gradle`
**Location**: `/service/build.gradle` lines 16-18

```gradle
implementation project(":fonts"),
        'net.sf.jasperreports:jasperreports',
        'net.sf.jasperreports:jasperreports-pdf',        // NEW
        'net.sf.jasperreports:jasperreports-excel-poi',  // NEW
        'commons-io:commons-io',
```

#### 3. `JasperReportConvertor.java`
**Location**: Multiple lines

```java
// Imports (lines 19-20, 37):
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.poi.export.JRXlsExporter;
import net.sf.jasperreports.pdf.SimplePdfReportConfiguration;

// Method (line 113-115):
private static Exporter createExporter(ReportExporter<? extends Exporter, ? extends ReportExportConfiguration> v) {
    try {
        return v.exporterClass.getDeclaredConstructor().newInstance();
    } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
        throw new RuntimeException(e);
    }
}
```

#### 4. `ReportWithOverflowTextAndBrokenImagesTest.java`
**Location**: Line 11

```java
import net.sf.jasperreports.pdf.JRPdfExporter;
```

---

## Build and Toolchain Requirements

### Current Requirements (Unchanged)

- **Java**: 17 (JDK/JRE) ✅
- **Gradle**: 8.12 ✅
- **Build Tool**: Gradle Wrapper included ✅

### New Dependencies Added

| Artifact | Version | Purpose | Size |
|----------|---------|---------|------|
| `jasperreports-pdf` | 7.0.3 | PDF export functionality | ~500KB |
| `jasperreports-excel-poi` | 7.0.3 | Excel (XLS/XLSX) export functionality | ~200KB |

### Transitive Dependency Updates

JasperReports 7.0.3 brings these updated transitive dependencies:

| Library | Old Version | New Version | Notes |
|---------|-------------|-------------|-------|
| Jackson Core | 2.x | 2.18.2 | JSON processing |
| Jackson Databind | 2.x | 2.18.2 | Data binding |
| Jackson XML | 2.x | 2.18.2 | XML processing |
| Batik | 1.x | 1.18 | SVG support |
| Commons Collections | 4.x | 4.4 | Utilities |

### Build Commands

```bash
# Clean build
./gradlew clean build

# Run tests only
./gradlew test

# Build fat JAR
./gradlew shadowJar

# Check dependencies
./gradlew dependencies
```

---

## Migration Plan

### Phase 1: Preparation ✅ COMPLETE

- [x] Analyze JasperReports 7.0.3 release notes
- [x] Identify breaking changes
- [x] Document impacted classes/methods
- [x] Create detailed migration plan
- [x] Assess risks and mitigation strategies

**Deliverables**: This document

### Phase 2: Code Updates ✅ COMPLETE

- [x] Update `build.gradle` with new dependency versions
- [x] Add `jasperreports-pdf` module dependency
- [x] Add `jasperreports-excel-poi` module dependency
- [x] Update imports in `JasperReportConvertor.java`
- [x] Fix deprecated `Class.newInstance()` usage
- [x] Update exception handling
- [x] Update test file imports
- [x] Verify code compiles successfully

**Deliverables**: Updated source files committed to branch

### Phase 3: Testing ⚠️ IN PROGRESS

- [x] Compile all source code
- [x] Compile all test code
- [ ] Run unit tests
- [ ] Verify all export formats (PDF, XLS, XLSX, DOC, DOCX, RTF, ODT, PPT, PPTX)
- [ ] Test template compilation
- [ ] Test subreport functionality
- [ ] Test XML data source handling
- [ ] Achieve 100% test pass rate

**Current Status**: 
- ✅ Compilation: Successful
- ⚠️ Tests: 9 failures, 6 passes
- 🔍 Investigation: XML loading issues in test environment

### Phase 4: Validation ⏸️ PENDING

- [ ] Confirm 100% test pass rate
- [ ] Perform end-to-end integration tests
- [ ] Test with real-world report templates
- [ ] Validate all supported export formats
- [ ] Document any behavioral changes
- [ ] Update README.md if needed

### Phase 5: Deployment ⏸️ PENDING

- [ ] Merge changes to main branch
- [ ] Tag release with new version
- [ ] Update CI/CD pipeline
- [ ] Deploy to production environment
- [ ] Monitor for issues

---

## Testing Strategy

### Test Categories

#### 1. Unit Tests
- **Target**: All existing JUnit 5 tests
- **Scope**: `service/src/test/java/com/microting/report/jasper/`
- **Status**: Compilation successful, runtime issues being investigated

#### 2. Integration Tests
- **DifferentOutputFormatsTest**: Tests all 9 export formats
- **FontExtensionsTest**: Tests font handling in PDF export
- **NegativeArgumentsTest**: Tests error handling ✅ PASSING
- **ReportWithOverflowTextAndBrokenImagesTest**: Tests edge cases ✅ PASSING

#### 3. Format-Specific Tests

| Format | Test Coverage | Status |
|--------|--------------|--------|
| PDF | ✅ Included | ⚠️ Issues |
| XLSX | ✅ Included | ⚠️ Issues |
| XLS | ✅ Included | ⚠️ Issues |
| DOCX | ✅ Included | ⚠️ Issues |
| DOC | ✅ Included | ⚠️ Issues |
| RTF | ✅ Included | ⚠️ Issues |
| ODT | ✅ Included | ⚠️ Issues |
| PPTX | ✅ Included | ⚠️ Issues |
| PPT | ✅ Included | ⚠️ Issues |

### Current Test Results

```
Total Tests: 15
✅ Passed: 6 (40%)
❌ Failed: 9 (60%)
```

**Passing Tests**:
- NegativeArgumentsTest.testArguments_WrongNumberOfArguments_Failed
- NegativeArgumentsTest.testArguments_UnsupportedArgument_Failed
- NegativeArgumentsTest.testArguments_WrongArgumentDeclaration_Failed
- ReportWithOverflowTextAndBrokenImagesTest (all 3 parameterized tests)

**Failing Tests**:
- DifferentOutputFormatsTest: All 8 format tests
- FontExtensionsTest: 1 test

**Failure Pattern**: All failures return exit code -50 (ERROR) instead of 0 (NORMAL)

---

## Known Issues

### 1. Test Failures in JasperExporter Integration Tests

**Status**: 🔍 Under Investigation

**Symptom**: 
- Exit code: -50 (ERROR) instead of 0 (NORMAL)
- Error message: "Unable to load report"
- Affects: `DifferentOutputFormatsTest` and `FontExtensionsTest`

**Impact**: MEDIUM
- Code compiles successfully
- Direct JasperReports API calls work (ReportWithOverflowTextAndBrokenImagesTest passes)
- Issue appears to be in the JasperExporter wrapper class integration

**Possible Causes**:
1. XML schema validation changes in JasperReports 7.0
2. ClassLoader issues in shadowJar packaging
3. Missing plugin descriptors in fat JAR
4. Path resolution differences in test environment

**Next Steps**:
1. Enable detailed logging to capture root cause exception
2. Test with individual modules instead of fat JAR
3. Check for XML namespace/schema compatibility
4. Verify all required JasperReports extensions are included

### 2. Original Codebase Build Failure

**Status**: ✅ Documented

**Discovery**: The original codebase with JasperReports 6.19.1 fails to build due to repository connectivity issues:
```
Could not resolve com.lowagie:itext:2.1.7.js9
Could not GET 'https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts/...'
```

**Impact**: 
- Cannot establish baseline test results with 6.19.1
- Acceptance criteria "all existing tests pass 100%" cannot be verified against original version
- Migration provides improved dependency resolution

**Mitigation**: JasperReports 7.0.3 uses updated dependencies that resolve correctly from Maven Central

### 3. Log4j2 Plugin Descriptor Warning

**Status**: ⚠️ Warning Only (Non-blocking)

**Message**:
```
No Log4j plugin descriptor was found in the classpath.
Falling back to scanning the `org.apache.logging.log4j.core` package.
```

**Impact**: LOW
- Does not prevent functionality
- May cause slightly slower startup
- Can be resolved by adding plugin descriptor to shadowJar

**Solution** (Optional):
```gradle
shadowJar {
    // Remove current exclude:
    // exclude "**/Log4j2Plugins.dat"
    
    // Or regenerate descriptors
    append 'META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat'
}
```

---

## Rollback Plan

### If Migration Fails

1. **Immediate Rollback**:
   ```bash
   git checkout HEAD~1  # Return to pre-migration commit
   ```

2. **Revert Dependencies**:
   ```gradle
   // In build.gradle:
   dependency 'net.sf.jasperreports:jasperreports:6.19.1'
   // Remove jasperreports-pdf and jasperreports-excel-poi
   ```

3. **Revert Code Changes**:
   - Restore original imports in `JasperReportConvertor.java`
   - Restore `Class.newInstance()` usage
   - Restore original exception handling

4. **Address Repository Issue**:
   - Configure alternative Maven repository for itext
   - Or use dependency substitution

### Alternative Approach

If JasperReports 7.0.3 proves problematic, consider:

1. **Stay on 6.19.1** with fixes:
   - Add alternative Maven repository for itext
   - Apply security patches separately
   - Plan migration for later date

2. **Gradual Migration**:
   - Update to intermediate version (6.20.x or 7.0.0)
   - Test thoroughly before moving to 7.0.3
   - Identify and fix issues incrementally

---

## Risk Assessment

| Risk | Likelihood | Impact | Severity | Mitigation |
|------|------------|--------|----------|------------|
| Test failures after upgrade | HIGH | HIGH | 🔴 CRITICAL | Thorough testing, investigation of root causes |
| API compatibility issues | LOW | MEDIUM | 🟡 MEDIUM | Well-documented changes, minimal code impact |
| Dependency conflicts | MEDIUM | MEDIUM | 🟡 MEDIUM | Use dependency management, test thoroughly |
| Production issues | MEDIUM | HIGH | 🔴 CRITICAL | Staged rollout, rollback plan ready |
| Performance degradation | LOW | MEDIUM | 🟡 MEDIUM | Performance testing, benchmarking |
| Breaking changes in minor version | MEDIUM | HIGH | 🔴 CRITICAL | Monitor JasperReports release notes |

### Mitigation Strategies

1. **Testing**:
   - Comprehensive unit test coverage
   - Integration testing with real reports
   - User acceptance testing
   - Load testing for performance validation

2. **Deployment**:
   - Deploy to staging environment first
   - Canary deployment strategy
   - Monitor error rates and performance metrics
   - Have rollback procedure documented and tested

3. **Documentation**:
   - Update all relevant documentation
   - Train team on new API patterns
   - Document any behavioral changes
   - Maintain this migration guide

---

## Recommendations

### Immediate Actions

1. **✅ COMPLETE**: Update dependencies and code for compilation
2. **🔍 IN PROGRESS**: Investigate and fix test failures
3. **📋 TODO**: Achieve 100% test pass rate
4. **📋 TODO**: Perform integration testing
5. **📋 TODO**: Deploy to staging for validation

### Best Practices

1. **Version Pinning**: Pin all transitive dependencies to avoid surprises
2. **Continuous Testing**: Run tests in CI/CD pipeline
3. **Monitoring**: Add application monitoring to detect issues early
4. **Documentation**: Keep this guide updated with findings
5. **Communication**: Inform stakeholders of migration status

### Future Considerations

1. **Stay Current**: Plan regular JasperReports updates (quarterly review)
2. **Monitor Releases**: Subscribe to JasperReports release announcements
3. **Test Coverage**: Expand test coverage for export functionality
4. **Performance**: Benchmark and optimize report generation
5. **Security**: Regularly audit dependencies for vulnerabilities

---

## Acceptance Criteria Status

| Criterion | Status | Notes |
|-----------|--------|-------|
| Document with checklist of impacted classes/methods | ✅ | See "Impacted Classes and Methods" section |
| Document build/toolchain needs | ✅ | See "Build and Toolchain Requirements" section |
| Provide a migration plan | ✅ | See "Migration Plan" section |
| Only accept if all existing tests pass 100% | ⚠️ | 60% passing (9/15 failing) - Investigation ongoing |

**Note**: The original codebase fails to build due to repository connectivity issues, making it impossible to establish a baseline for "all existing tests pass 100%". The migration improves dependency resolution but introduces test failures that require further investigation.

---

## Conclusion

The migration from JasperReports 6.19.1 to 7.0.3 involves **significant but well-defined breaking changes**. The primary impacts are:

1. **Modularization** of PDF and Excel exporters (requires new dependencies)
2. **API modernization** removing deprecated Java reflection methods
3. **Package relocations** for exporter classes

**Current Status**: 
- ✅ All code changes implemented
- ✅ Code compiles successfully  
- ⚠️ Test failures under investigation

**Risk Assessment**: MEDIUM
- Changes are well-documented and understood
- Code modifications are minimal (10 lines across 4 files)
- Test failures suggest integration issues rather than API problems
- Original codebase has existing build issues

**Recommendation**: 
- Continue investigation of test failures
- Resolve XML loading issues in test environment
- Proceed with migration once 100% test pass rate achieved
- Benefits of JasperReports 7.0.3 (security updates, better Java 17 support) outweigh migration effort

---

*Document Version*: 1.0
*Last Updated*: 2025-09-30
*Author*: GitHub Copilot
*Status*: Draft - Pending Test Resolution
