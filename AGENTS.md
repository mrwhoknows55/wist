# Android 80-20 Rule: High-Impact Practices

1. **Never hardcode colors** - Use `MaterialTheme.colorScheme` in Compose, `colors.xml` in XML
2. **Don't catch CancellationException** - Let it propagate, breaks coroutine cancellation
3. **Avoid GlobalScope** - Use `viewModelScope`/`lifecycleScope`, ties to lifecycle
4. **Single Activity + Navigation** - One activity, navigate with NavController
5. **Stateless Composables** - Hoist state up, pass callbacks down
6. **Remember expensive operations** - Use `remember`, `derivedStateOf`, `LaunchedEffect`
7. **Proper lifecycle collection** - `collectAsStateWithLifecycle()` or `repeatOnLifecycle`
8. **Responsive layouts** - Use `WindowSizeClass`, adaptive layouts, avoid fixed sizes
9. **Dependency Injection** - Hilt/Koin, inject ViewModels and repositories
10. **Sealed classes for state** - UI/Loading/Error states, no nulls or booleans
11. **Repository pattern** - Separate data layer, Room as single source of truth
12. **Background dispatchers** - IO for network/DB, Default for CPU work, never Main
13. **Immutable data** - `val` over `var`, use `copy()` for updates
14. **Modifier order matters** - Size before padding, clickable after padding
15. **ProGuard/R8 + shrinkResources** - Enable for release, keep rules updated