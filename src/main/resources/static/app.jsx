const { useEffect, useState } = React;
const AUTH_STORAGE_KEY = "job-search-auth";

const routeLabels = {
    home: "Главная",
    vacancies: "Вакансии",
    companies: "Компании",
    users: "Кандидаты",
    resumes: "Резюме",
    employers: "Работодатели",
    applications: "Отклики"
};

const modeConfigs = {
    seeker: {
        label: "Ищу вакансию",
        badge: "Сценарий соискателя",
        routes: ["vacancies", "companies", "resumes", "applications"],
        heroTitle: "Найдите работу, соберите сильное резюме и откликайтесь в одном интерфейсе.",
        heroText: "Этот режим показывает то, что важно соискателю: поиск вакансий, просмотр компаний, управление резюме и контроль собственных откликов.",
        primaryAction: { text: "Создать резюме", route: "resumes" },
        secondaryAction: { text: "Смотреть вакансии", route: "vacancies" },
        searchTitlePlaceholder: "Должность или вакансия",
        searchLocationPlaceholder: "Город или формат работы",
        searchButtonText: "Найти вакансии",
        searchNote: "Поиск идет по API вакансий и открывает список с уже примененными фильтрами.",
        searchRoute: "vacancies",
        sideTitle: "Инструменты соискателя",
        spotlightLabel: "Открытых вакансий",
        spotlightValueKey: "vacancies",
        spotlightMetaLeft: "Компаний",
        spotlightMetaLeftKey: "companies",
        spotlightMetaRight: "Откликов",
        spotlightMetaRightKey: "applications",
        homeTitle: "Рабочее место соискателя",
        homeText: "Сначала просмотрите вакансии и компании, затем ведите резюме и отклики без лишних переходов.",
        homeHighlights: [
            "Фильтрация вакансий по названию, локации, зарплате и компании.",
            "Связь OneToMany: пользователь -> резюме.",
            "Связь ManyToMany: пользователь -> навыки."
        ]
    },
    employer: {
        label: "Ищу сотрудника",
        badge: "Сценарий работодателя",
        routes: ["companies", "employers", "vacancies", "resumes", "users", "applications"],
        heroTitle: "Публикуйте вакансии, управляйте работодателями и быстро просматривайте кандидатов.",
        heroText: "Этот режим собран под найм: компании, работодатели, вакансии, кандидаты и поток откликов на одном SPA-экране.",
        primaryAction: { text: "Создать вакансию", route: "vacancies" },
        secondaryAction: { text: "Смотреть отклики", route: "applications" },
        searchTitlePlaceholder: "Навык, роль или резюме",
        searchLocationPlaceholder: "Город кандидата",
        searchButtonText: "Найти кандидатов",
        searchNote: "В режиме найма поиск ведет по резюме: навык или роль, локация и быстрый переход к кандидатам.",
        searchRoute: "resumes",
        sideTitle: "Инструменты работодателя",
        spotlightLabel: "Активных вакансий",
        spotlightValueKey: "vacancies",
        spotlightMetaLeft: "Работодателей",
        spotlightMetaLeftKey: "employers",
        spotlightMetaRight: "Кандидатов",
        spotlightMetaRightKey: "users",
        homeTitle: "Рабочее место работодателя",
        homeText: "Основной поток: открыть компанию, управлять работодателями, разместить вакансию и обработать отклики кандидатов.",
        homeHighlights: [
            "Связь OneToMany: компания -> вакансии и работодатели.",
            "Просмотр кандидатов и их навыков из режима найма.",
            "Обработка откликов и быстрые действия по статусам."
        ]
    }
};

const initialEntityState = {
    vacancies: {
        items: [],
        filters: { title: "", location: "", minSalary: "", maxExperience: "", companyId: "" },
        detail: null
    },
    companies: {
        items: [],
        filters: { industry: "", keyword: "" },
        detail: null
    },
    users: {
        items: [],
        filters: { status: "" },
        detail: null
    },
    resumes: {
        items: [],
        filters: { skill: "", location: "", maxSalary: "" },
        detail: null
    },
    employers: {
        items: [],
        filters: { companyId: "", email: "" },
        detail: null
    },
    applications: {
        items: [],
        filters: { userId: "", status: "", vacancyTitle: "", resumeTitle: "" },
        detail: null
    }
};

const forms = {
    companies: {
        title: "Компания",
        endpoint: "/api/companies",
        supportsUpdate: true,
        fields: [
            field("name", "Название компании", "text", true),
            field("industry", "Отрасль", "text"),
            field("location", "Локация", "text"),
            field("website", "Сайт", "text"),
            field("contactEmail", "Email", "email"),
            field("contactPhone", "Телефон", "text"),
            field("description", "Описание", "textarea", false, true)
        ]
    },
    users: {
        title: "Кандидат",
        endpoint: "/api/users",
        supportsUpdate: true,
        fields: [
            field("firstName", "Имя", "text", true),
            field("lastName", "Фамилия", "text", true),
            field("email", "Email", "email", true),
            field("phoneNumber", "Телефон", "text")
        ]
    },
    resumes: {
        title: "Резюме",
        endpoint: "/api/resumes",
        supportsUpdate: true,
        fields: [
            field("title", "Название", "text", true),
            field("userId", "ID пользователя", "number", true),
            field("skills", "Навыки", "textarea", false, true),
            field("experience", "Опыт", "textarea", false, true),
            field("education", "Образование", "textarea", false, true),
            field("expectedSalary", "Ожидаемая зарплата", "number"),
            field("location", "Локация", "text")
        ]
    },
    employers: {
        title: "Работодатель",
        endpoint: "/api/employers",
        supportsUpdate: true,
        fields: [
            field("firstName", "Имя", "text", true),
            field("lastName", "Фамилия", "text", true),
            field("email", "Email", "email", true),
            field("phoneNumber", "Телефон", "text"),
            field("companyId", "ID компании", "number", true)
        ]
    },
    vacancies: {
        title: "Вакансия",
        endpoint: "/api/vacancies",
        supportsUpdate: true,
        fields: [
            field("title", "Название", "text", true),
            field("salary", "Зарплата", "number", true),
            field("requiredExperience", "Опыт, лет", "number"),
            field("location", "Локация", "text"),
            field("createdById", "ID работодателя", "number", true),
            field("description", "Описание", "textarea", false, true)
        ]
    },
    applications: {
        title: "Отклик",
        endpoint: "/api/applications",
        supportsUpdate: false,
        fields: [
            field("userId", "ID пользователя", "number", true),
            field("vacancyId", "ID вакансии", "number", true),
            field("resumeId", "ID резюме", "number", true),
            field("coverLetter", "Сопроводительное письмо", "textarea", false, true)
        ]
    }
};

const entityConfigs = {
    vacancies: {
        subtitle: "Поиск и управление вакансиями.",
        filters: [
            filterField("title", "Название", "text"),
            filterField("location", "Локация", "text"),
            filterField("minSalary", "Мин. зарплата", "number"),
            filterField("maxExperience", "Макс. опыт", "number")
        ]
    },
    companies: {
        subtitle: "Компании и их связи с вакансиями и работодателями.",
        filters: [
            filterField("industry", "Отрасль", "text"),
            filterField("keyword", "Ключевое слово", "text")
        ]
    },
    users: {
        subtitle: "Кандидаты, резюме и связанные навыки.",
        filters: [
            filterField("status", "Статус", "text")
        ]
    },
    resumes: {
        subtitle: "Резюме кандидатов и связь OneToMany.",
        filters: [
            filterField("skill", "Навык", "text"),
            filterField("location", "Локация", "text"),
            filterField("maxSalary", "Макс. зарплата", "number")
        ]
    },
    employers: {
        subtitle: "Работодатели, привязанные к компаниям.",
        filters: [
            filterField("email", "Email", "email")
        ]
    },
    applications: {
        subtitle: "Отклики с фильтрацией и быстрыми действиями.",
        filters: [
            filterField("status", "Статус", "text"),
            filterField("vacancyTitle", "Вакансия", "text"),
            filterField("resumeTitle", "Резюме", "text")
        ]
    }
};

function App() {
    const [route, setRoute] = useState(getRouteFromHash());
    const [mode, setMode] = useState("seeker");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [auth, setAuth] = useState(getStoredAuth());
    const [authModal, setAuthModal] = useState(null);
    const [modal, setModal] = useState(null);
    const [detailModal, setDetailModal] = useState(null);
    const [heroFilters, setHeroFilters] = useState({ title: "", location: "" });
    const [resumeView, setResumeView] = useState("all");
    const [entityState, setEntityState] = useState(initialEntityState);

    const modeConfig = modeConfigs[mode];
    const visibleRoutes = getVisibleRoutes(mode, auth);
    const stats = {
        vacancies: getDisplayedItems("vacancies", entityState.vacancies).length,
        companies: getDisplayedItems("companies", entityState.companies).length,
        users: getDisplayedItems("users", entityState.users).length,
        resumes: getDisplayedItems("resumes", entityState.resumes).length,
        employers: getDisplayedItems("employers", entityState.employers).length,
        applications: getDisplayedItems("applications", entityState.applications).length
    };

    useEffect(() => {
        const onHashChange = () => setRoute(getRouteFromHash());
        window.addEventListener("hashchange", onHashChange);
        return () => window.removeEventListener("hashchange", onHashChange);
    }, []);

    useEffect(() => {
        restoreAuthSession();
    }, []);

    useEffect(() => {
        renderVisibleStateImmediately();
        loadAll();
    }, []);

    useEffect(() => {
        if (auth?.token) {
            loadAll();
        }
    }, [auth?.token, auth?.role, auth?.userId]);

    useEffect(() => {
        const frame = window.requestAnimationFrame(() => {
            repairRenderedContent(document.getElementById("app"));
        });

        return () => window.cancelAnimationFrame(frame);
    }, [route, mode, auth, modal, authModal, detailModal, entityState, resumeView, error, success, loading]);

    useEffect(() => {
        if (route === "resumes") {
            loadEntity("resumes", true);
        }
    }, [resumeView]);

    useEffect(() => {
        if (route !== "home" && !visibleRoutes.includes(route)) {
            goToRoute("home");
        }
    }, [route, visibleRoutes]);

    function renderVisibleStateImmediately() {
        setLoading(false);
        setError("");
        setSuccess("");
    }

    async function restoreAuthSession() {
        const storedAuth = getStoredAuth();
        if (!storedAuth?.token) {
            return;
        }

        try {
            const session = await request("/api/auth/me");
            setAuth(session);
            persistAuth(session);
            syncModeWithRole(session.role);
        } catch {
            clearStoredAuth();
            setAuth(null);
        }
    }

    function openAuthModal(kind) {
        setAuthModal({
            kind,
            role: mode === "employer" ? "EMPLOYER" : "SEEKER"
        });
    }

    function syncModeWithRole(role) {
        if (role === "EMPLOYER") {
            setMode("employer");
            return;
        }
        if (role === "SEEKER") {
            setMode("seeker");
        }
    }

    function ensureAccess(entity, action = "manage") {
        const requiredRole = getRequiredRole(entity, action);

        if (!requiredRole) {
            return true;
        }

        if (!auth?.token) {
            setError("");
            setSuccess("");
            openAuthModal(action === "create" ? "register" : "login");
            return false;
        }

        if (auth.role !== requiredRole) {
            setError(requiredRole === "EMPLOYER"
                ? "Для этого действия нужен вход в роли работодателя."
                : "Для этого действия нужен вход в роли соискателя.");
            return false;
        }

        return true;
    }

    async function loadAll() {
        setLoading(true);
        const entities = Object.keys(initialEntityState);
        const results = await Promise.allSettled(entities.map((entity) => loadEntity(entity, false)));
        const failed = results.find((result) => result.status === "rejected");
        if (failed) {
            setError(failed.reason?.message || "Часть данных API не загрузилась.");
        }
        setLoading(false);
    }

    async function loadEntity(entity, toggleLoading = true, overrideFilters = null) {
        if (toggleLoading) {
            setLoading(true);
        }
        try {
            const items = await fetchEntityItems(entity, overrideFilters || entityState[entity].filters, {
                auth,
                mode,
                resumeView
            });
            setEntityState((current) => ({
                ...current,
                [entity]: {
                    ...current[entity],
                    items
                }
            }));
        } finally {
            if (toggleLoading) {
                setLoading(false);
            }
        }
    }

    async function loadRelations(entity, id) {
        try {
            setError("");
            setLoading(true);

            if (entity === "companies") {
                const [employers, vacancies] = await Promise.all([
                    requestList(`/api/employers/company/${id}`),
                    requestList(`/api/vacancies${toQueryString({ companyId: id })}`)
                ]);
                setDetailModal({
                    route: entity,
                    title: "Связи компании",
                    detail: { employers, vacancies }
                });
            }

            if (entity === "users") {
                const user = entityState.users.items.find((item) => String(item.id) === String(id));
                const resumes = await requestList(`/api/resumes/user/${id}`);
                setDetailModal({
                    route: entity,
                    title: "Связи кандидата",
                    detail: { resumes, skills: user?.skills || [] }
                });
            }

            if (entity === "vacancies") {
                const applications = await requestList(`/api/applications/vacancy/${id}`);
                setDetailModal({
                    route: entity,
                    title: "Отклики по вакансии",
                    detail: { applications }
                });
            }
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setLoading(false);
        }
    }

    function updateFilter(entity, name, value) {
        setEntityState((current) => ({
            ...current,
            [entity]: {
                ...current[entity],
                filters: {
                    ...current[entity].filters,
                    [name]: value
                }
            }
        }));
    }

    async function handleReset(entity) {
        const emptyFilters = Object.fromEntries(
            Object.keys(entityState[entity].filters).map((key) => [key, ""])
        );

        setEntityState((current) => ({
            ...current,
            [entity]: {
                ...current[entity],
                filters: emptyFilters,
                detail: null
            }
        }));

        await loadEntity(entity, true, emptyFilters);
    }

    async function submitAuth(formData, kind) {
        try {
            setError("");
            setSuccess("");
            setLoading(true);
            const response = await request(`/api/auth/${kind}`, {
                method: "POST",
                body: JSON.stringify(formData)
            });
            setAuth(response);
            persistAuth(response);
            syncModeWithRole(response.role);
            setAuthModal(null);
            setSuccess(kind === "login" ? "Вход выполнен успешно." : "Регистрация выполнена успешно.");
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setLoading(false);
        }
    }

    async function createRegistrationCompany(formData) {
        const company = await request("/api/auth/company", {
            method: "POST",
            body: JSON.stringify(cleanParams(formData))
        });

        setEntityState((current) => {
            const existing = current.companies.items || [];
            const nextItems = existing.some((item) => String(item.id) === String(company.id))
                ? existing
                : [company, ...existing];

            return {
                ...current,
                companies: {
                    ...current.companies,
                    items: nextItems
                }
            };
        });

        return company;
    }

    async function handleLogout() {
        try {
            if (auth?.token) {
                await request("/api/auth/logout", { method: "POST" });
            }
        } catch {
            // ignore logout network errors and clear client session anyway
        } finally {
            clearStoredAuth();
            setAuth(null);
            setSuccess("Вы вышли из аккаунта.");
        }
    }

    async function submitForm(formData) {
        if (!ensureAccess(modal.entity, modal.mode === "edit" ? "edit" : "create")) {
            return;
        }

        const config = forms[modal.entity];
        let normalizedFormData = formData;

        if (modal.entity === "applications") {
            const selectedResume = entityState.resumes.items.find((item) => String(item.id) === String(formData.resumeId));
            normalizedFormData = {
                ...formData,
                userId: selectedResume?.userId ?? formData.userId
            };
        }

        const payload = collectPayload(normalizedFormData, config.fields);
        try {
            setError("");
            setSuccess("");
            setLoading(true);
            const isEdit = modal.mode === "edit" && config.supportsUpdate;
            await request(isEdit ? `${config.endpoint}/${modal.item.id}` : config.endpoint, {
                method: isEdit ? "PUT" : "POST",
                body: JSON.stringify(payload)
            });
            setModal(null);
            setSuccess(`${config.title} ${isEdit ? "обновлен" : "создан"} успешно.`);
            await loadEntity(modal.entity, false);
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setLoading(false);
        }
    }

    async function deleteItem(entity, id) {
        if (!ensureAccess(entity, "delete")) {
            return;
        }
        if (!window.confirm(`Удалить запись #${id}?`)) {
            return;
        }
        try {
            setError("");
            setSuccess("");
            setLoading(true);
            await request(`${forms[entity].endpoint}/${id}`, { method: "DELETE" });
            setSuccess(`${forms[entity].title} удален успешно.`);
            await loadEntity(entity, false);
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setLoading(false);
        }
    }

    async function patchEntity(entity, id, patch) {
        const action = entity === "applications" && patch.startsWith("status") ? "status" : "edit";
        if (!ensureAccess(entity, action)) {
            return;
        }
        try {
            setError("");
            setSuccess("");
            setLoading(true);
            const url = entity === "applications" && patch.startsWith("status")
                ? `/api/applications/${id}/${patch}`
                : `${forms[entity].endpoint}/${id}/${patch}`;
            await request(url, { method: "PATCH" });
            setSuccess(`${forms[entity].title}: состояние обновлено.`);
            await loadEntity(entity, false);
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setLoading(false);
        }
    }

    function openModal(entity, item = null, modeOverride = null) {
        const action = modeOverride === "create" || !item?.id ? "create" : "edit";
        if (!ensureAccess(entity, action)) {
            return;
        }
        const config = forms[entity];
        setModal({
            entity,
            item,
            mode: modeOverride || (item?.id && config.supportsUpdate ? "edit" : "create")
        });
    }

    function goToRoute(nextRoute) {
        setRoute(nextRoute);
        window.location.hash = `#${nextRoute}`;
    }

    function handleHeroSearch() {
        if (mode === "seeker") {
            const filters = {
                ...entityState.vacancies.filters,
                title: heroFilters.title.trim(),
                location: heroFilters.location.trim()
            };
            setEntityState((current) => ({
                ...current,
                vacancies: {
                    ...current.vacancies,
                    filters
                }
            }));
            goToRoute("vacancies");
            loadEntity("vacancies", true, filters);
            return;
        }

        const filters = {
            ...entityState.resumes.filters,
            skill: heroFilters.title.trim(),
            location: heroFilters.location.trim()
        };
        setEntityState((current) => ({
            ...current,
            resumes: {
                ...current.resumes,
                filters
            }
        }));
        goToRoute("resumes");
        loadEntity("resumes", true, filters);
    }

    function handleModeChange(nextMode) {
        setMode(nextMode);
        const nextRoutes = modeConfigs[nextMode].routes;
        if (route !== "home" && !nextRoutes.includes(route)) {
            goToRoute(nextRoutes[0]);
        }
    }

    return (
        <div className="page-shell">
            <Topbar
                route={route}
                mode={mode}
                auth={auth}
                visibleRoutes={visibleRoutes}
                onModeChange={handleModeChange}
                onLogin={() => openAuthModal("login")}
                onRegister={() => openAuthModal("register")}
                onLogout={handleLogout}
            />
            {route === "home" ? (
                <Hero
                    mode={mode}
                    route={route}
                    stats={stats}
                    heroFilters={heroFilters}
                    setHeroFilters={setHeroFilters}
                    onSearch={handleHeroSearch}
                    onPrimaryAction={() => goToRoute(modeConfig.primaryAction.route)}
                    onSecondaryAction={() => goToRoute(modeConfig.secondaryAction.route)}
                />
            ) : null}
            <div className="content">
                <main className="main">
                    <Notices error={error} success={success} loading={loading} />
                    {route === "home" ? (
                        <HomeView mode={mode} stats={stats} />
                    ) : (
                        <EntityPage
                            mode={mode}
                            auth={auth}
                            route={route}
                            config={entityConfigs[route]}
                            state={entityState[route]}
                            loading={loading}
                            resumeView={resumeView}
                            onResumeViewChange={setResumeView}
                            onOpenCreate={() => openModal(route)}
                            onRefresh={() => loadEntity(route)}
                            onFilterChange={updateFilter}
                            onApplyFilters={() => loadEntity(route)}
                            onResetFilters={handleReset}
                            onOpenEdit={(item) => openModal(route, item)}
                            onOpenApply={(vacancy) => openModal("applications", { vacancyId: vacancy.id })}
                            onOpenCopy={(item) => openModal(route, item, "create")}
                            onDelete={deleteItem}
                            onPatch={patchEntity}
                            onRelations={loadRelations}
                        />
                    )}
                </main>
            </div>
            {modal ? (
                <FormModal
                    modal={modal}
                    auth={auth}
                    entityState={entityState}
                    onClose={() => setModal(null)}
                    onSubmit={submitForm}
                />
            ) : null}
            {authModal ? (
                <AuthModal
                    authModal={authModal}
                    companies={entityState.companies.items}
                    onCreateCompany={createRegistrationCompany}
                    onClose={() => setAuthModal(null)}
                    onSubmit={submitAuth}
                />
            ) : null}
            {detailModal ? (
                <DetailModal
                    detailModal={detailModal}
                    onClose={() => setDetailModal(null)}
                />
            ) : null}
        </div>
    );
}

function Topbar({ route, mode, auth, visibleRoutes, onModeChange, onLogin, onRegister, onLogout }) {
    return (
        <header className="topbar">
            <div className="topbar-inner">
                <a className="brand" href="#home">
                    <div className="brand-badge">J</div>
                    <div className="brand-text">
                        <div className="brand-title">Job Search</div>
                    </div>
                </a>
                <div className="mode-switcher">
                    {Object.entries(modeConfigs).map(([key, config]) => (
                        <button
                            key={key}
                            className={`mode-switch ${mode === key ? "active" : ""}`}
                            onClick={() => onModeChange(key)}
                            type="button">
                            <span className="mode-switch-title">{config.label}</span>
                        </button>
                    ))}
                </div>
                <nav className="nav">
                    <a className={`nav-link ${route === "home" ? "active" : ""}`} href="#home">Главная</a>
                    {visibleRoutes.map((item) => (
                        <a
                            key={item}
                            className={`nav-link ${route === item ? "active" : ""}`}
                            href={`#${item}`}>
                            {routeLabels[item]}
                        </a>
                    ))}
                </nav>
                <div className="auth-actions">
                    {auth?.token ? (
                        <>
                            <div className="auth-user">{auth.displayName}</div>
                            <button className="button button-secondary auth-button" type="button" onClick={onLogout}>Выйти</button>
                        </>
                    ) : (
                        <>
                            <button className="button button-secondary auth-button" type="button" onClick={onLogin}>Войти</button>
                            <button className="button button-primary auth-button" type="button" onClick={onRegister}>Зарегистрироваться</button>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}

function Hero({
    mode,
    stats,
    heroFilters,
    setHeroFilters,
    onSearch,
    onPrimaryAction,
    onSecondaryAction
}) {
    const config = modeConfigs[mode];

    return (
        <section className="hero">
            <div className="hero-inner">
                <div className="hero-copy">
                    <div className="hero-mode-badge">{config.badge}</div>
                    <div className="hero-search">
                        <form
                            className="hero-search-grid"
                            onSubmit={(event) => {
                                event.preventDefault();
                                onSearch();
                            }}>
                            <input
                                className="input"
                                type="text"
                                placeholder={config.searchTitlePlaceholder}
                                value={heroFilters.title}
                                onChange={(event) => setHeroFilters((current) => ({ ...current, title: event.target.value }))}
                            />
                            <input
                                className="input"
                                type="text"
                                placeholder={config.searchLocationPlaceholder}
                                value={heroFilters.location}
                                onChange={(event) => setHeroFilters((current) => ({ ...current, location: event.target.value }))}
                            />
                            <button className="button button-primary" type="submit">{config.searchButtonText}</button>
                        </form>
                    </div>
                    <div className="hero-cta-row">
                        <button className="button button-primary" type="button" onClick={onPrimaryAction}>
                            {config.primaryAction.text}
                        </button>
                        <button className="button button-secondary" type="button" onClick={onSecondaryAction}>
                            {config.secondaryAction.text}
                        </button>
                    </div>
                </div>
            </div>
        </section>
    );
}

function HomeView({ mode, stats }) {
    const cards = mode === "seeker"
        ? [
            ["Вакансии", stats.vacancies],
            ["Компании", stats.companies],
            ["Резюме", stats.resumes],
            ["Отклики", stats.applications]
        ]
        : [
            ["Компании", stats.companies],
            ["Работодатели", stats.employers],
            ["Вакансии", stats.vacancies],
            ["Кандидаты", stats.users]
        ];

    return (
        <section className="panel">
            <div className="panel-header">
                <div>
                    <h2 className="panel-title">Статистика платформы</h2>
                </div>
            </div>
            <div className="panel-body">
                <div className="stats-grid">
                    {cards.map(([label, value]) => <StatCard key={label} label={label} value={value} />)}
                </div>
            </div>
        </section>
    );
}

function EntityPage({
    mode,
    auth,
    route,
    config,
    state,
    loading,
    resumeView,
    onResumeViewChange,
    onOpenCreate,
    onRefresh,
    onFilterChange,
    onApplyFilters,
    onResetFilters,
    onOpenEdit,
    onOpenApply,
    onOpenCopy,
    onDelete,
    onPatch,
    onRelations
}) {
    const requiredCreateRole = route === "home" ? null : getRequiredRole(route, "create");
    const canCreate = route !== "home" && (!requiredCreateRole || auth?.role === requiredCreateRole);
    const canSwitchResumeView = route === "resumes" && mode === "seeker" && auth?.userId;
    const visibleItems = getDisplayedItems(route, state);

    return (
        <section className="panel">
            <div className="panel-header">
                <div>
                    <h2 className="panel-title">{routeLabels[route]}</h2>
                </div>
                <div className="toolbar">
                    {canSwitchResumeView ? (
                        <div className="inline-switch">
                            <button
                                className={`inline-switch-button ${resumeView === "all" ? "active" : ""}`}
                                type="button"
                                onClick={() => onResumeViewChange("all")}>
                                Все резюме
                            </button>
                            <button
                                className={`inline-switch-button ${resumeView === "mine" ? "active" : ""}`}
                                type="button"
                                onClick={() => onResumeViewChange("mine")}>
                                Мои резюме
                            </button>
                        </div>
                    ) : null}
                    <button className="button button-secondary" type="button" onClick={onRefresh}>Обновить</button>
                    {canCreate ? (
                        <button className="button button-primary" type="button" onClick={onOpenCreate}>Создать</button>
                    ) : null}
                </div>
            </div>
            <div className="panel-body">
                <FilterPanel
                    route={route}
                    filters={state.filters}
                    fields={config.filters}
                    onChange={onFilterChange}
                    onApply={onApplyFilters}
                    onReset={onResetFilters}
                />
                {loading && visibleItems.length === 0 ? (
                    <div className="empty-state">
                        <strong>Загрузка данных</strong>
                        <span>Ждем ответ от backend API...</span>
                    </div>
                ) : null}
                {!loading && visibleItems.length === 0 ? (
                    <div className="empty-state">
                        <strong>Ничего не найдено</strong>
                        <span>Попробуйте изменить фильтры или создать новую запись.</span>
                    </div>
                ) : null}
                {visibleItems.length > 0 ? (
                    <div className="grid-cards">
                        {visibleItems.map((item) => (
                            <EntityCard
                                auth={auth}
                                mode={mode}
                                key={`${route}-${item.id}`}
                                route={route}
                                item={item}
                                onOpenEdit={onOpenEdit}
                                onOpenApply={onOpenApply}
                                onOpenCopy={onOpenCopy}
                                onDelete={onDelete}
                                onPatch={onPatch}
                                onRelations={onRelations}
                            />
                        ))}
                    </div>
                ) : null}
            </div>
        </section>
    );
}

function FilterPanel({ route, filters, fields, onChange, onApply, onReset }) {
    return (
        <div className="filters">
            <div className="filters-grid">
                {fields.map((item) => (
                    <div className="field" key={item.name}>
                        <label>{item.label}</label>
                        <input
                            className="input"
                            type={item.type}
                            value={filters[item.name]}
                            onChange={(event) => onChange(route, item.name, event.target.value)}
                        />
                    </div>
                ))}
            </div>
            <div className="button-row">
                <button className="button button-primary" type="button" onClick={onApply}>Применить</button>
                <button className="button button-secondary" type="button" onClick={() => onReset(route)}>Сбросить</button>
            </div>
        </div>
    );
}

function EntityCard({ auth, mode, route, item, onOpenEdit, onOpenApply, onOpenCopy, onDelete, onPatch, onRelations }) {
    if (route === "vacancies") {
        const canManageVacancies = auth?.role === "EMPLOYER"
            && auth?.companyId
            && String(auth.companyId) === String(item.companyId);
        return (
            <article className="card">
                <div className="card-top">
                    <div>
                        <h3 className="card-title">{item.title}</h3>
                        <div className="card-subtitle">{item.companyName || "Компания не указана"} | {item.location || "Локация не указана"}</div>
                    </div>
                    <div className="card-price">{formatMoney(item.salary)}</div>
                </div>
                <div className="badge-row">
                    <StatusBadge status={item.status} />
                    <span className="badge">Опыт: {item.requiredExperience ?? 0} г.</span>
                </div>
                <div className="card-subtitle">{truncateText(item.description || "Описание отсутствует.", 180)}</div>
                {canManageVacancies ? (
                    <div className="action-row">
                        <button className="button button-secondary" onClick={() => onRelations("vacancies", item.id)}>Отклики</button>
                        <button className="button button-secondary" onClick={() => onOpenEdit({
                            id: item.id,
                            title: item.title,
                            salary: item.salary,
                            requiredExperience: item.requiredExperience,
                            location: item.location,
                            description: item.description,
                            createdById: item.createdById
                        })}>Изменить</button>
                        <button className="button button-accent" onClick={() => onPatch("vacancies", item.id, "close")}>Закрыть</button>
                        <button className="button button-danger" onClick={() => onDelete("vacancies", item.id)}>Удалить</button>
                    </div>
                ) : (
                    <div className="action-row">
                        <button className="button button-primary" onClick={() => onOpenApply(item)}>Откликнуться</button>
                    </div>
                )}
            </article>
        );
    }

    if (route === "companies") {
        const canManageCompanies = auth?.role === "EMPLOYER" && auth?.companyId && String(auth.companyId) === String(item.id);
        return (
            <article className="card">
                <div className="card-top">
                    <div>
                        <h3 className="card-title">{item.name}</h3>
                        <div className="card-subtitle">{item.industry || "Отрасль не указана"} | {item.location || "Локация не указана"}</div>
                    </div>
                </div>
                <div className="meta-row">
                    <span className="meta">Email: {item.contactEmail || "-"}</span>
                    <span className="meta">Телефон: {item.contactPhone || "-"}</span>
                </div>
                <div className="card-subtitle">{truncateText(item.description || "Описание отсутствует.", 180)}</div>
                <div className="action-row">
                    <button className="button button-secondary" onClick={() => onRelations("companies", item.id)}>Связи</button>
                    {canManageCompanies ? (
                        <button className="button button-secondary" onClick={() => onOpenEdit(item)}>Изменить</button>
                    ) : null}
                    {canManageCompanies ? (
                        <button className="button button-danger" onClick={() => onDelete("companies", item.id)}>Удалить</button>
                    ) : null}
                </div>
            </article>
        );
    }

    if (route === "users") {
        const canManageUsers = false;
        return (
            <article className="card">
                <div className="card-top">
                    <div>
                        <h3 className="card-title">{`${item.firstName || ""} ${item.lastName || ""}`.trim()}</h3>
                        <div className="card-subtitle">{item.email || "Email не указан"} | {item.phoneNumber || "Телефон не указан"}</div>
                    </div>
                </div>
                <div className="badge-row">
                    <StatusBadge status={item.status} />
                </div>
                <div className="tag-row">
                    {(item.skills || []).length
                        ? item.skills.map((skill) => <span className="tag" key={skill.id}>{skill.name}</span>)
                        : <span className="tag">Навыки не указаны</span>}
                </div>
                <div className="action-row">
                    <button className="button button-secondary" onClick={() => onRelations("users", item.id)}>Резюме</button>
                    {canManageUsers ? (
                        <button className="button button-secondary" onClick={() => onOpenEdit(item)}>Изменить</button>
                    ) : null}
                    {canManageUsers ? (
                        <button className="button button-accent" onClick={() => onPatch("users", item.id, "block")}>Блокировать</button>
                    ) : null}
                    {canManageUsers ? (
                        <button className="button button-danger" onClick={() => onDelete("users", item.id)}>Удалить</button>
                    ) : null}
                </div>
            </article>
        );
    }

    if (route === "resumes") {
        const canManageResume = auth?.role === "SEEKER" && auth?.userId && String(auth.userId) === String(item.userId);
        return (
            <article className="card">
                <div className="card-top">
                    <div>
                        <h3 className="card-title">{item.title}</h3>
                        <div className="card-subtitle">{item.userFullName || "Пользователь не найден"} | {item.location || "Локация не указана"}</div>
                    </div>
                    <div className="card-price">{formatMoney(item.expectedSalary)}</div>
                </div>
                <div className="badge-row">
                    <StatusBadge status={item.status} />
                </div>
                <div className="card-subtitle">{truncateText(item.skills || "Навыки не заполнены.", 180)}</div>
                {canManageResume ? (
                    <div className="action-row">
                        <button className="button button-secondary" onClick={() => onOpenEdit({
                            id: item.id,
                            title: item.title,
                            userId: item.userId,
                            skills: item.skills,
                            experience: item.experience,
                            education: item.education,
                            expectedSalary: item.expectedSalary,
                            location: item.location
                        })}>Изменить</button>
                        <button className="button button-accent" onClick={() => onPatch("resumes", item.id, "hide")}>Скрыть</button>
                        <button className="button button-danger" onClick={() => onDelete("resumes", item.id)}>Удалить</button>
                    </div>
                ) : null}
            </article>
        );
    }

    if (route === "employers") {
        const canManageEmployers = auth?.role === "EMPLOYER" && auth?.companyId && String(auth.companyId) === String(item.companyId);
        return (
            <article className="card">
                <div className="card-top">
                    <div>
                        <h3 className="card-title">{`${item.firstName || ""} ${item.lastName || ""}`.trim()}</h3>
                        <div className="card-subtitle">{item.companyName || "Компания не указана"} | {item.email || "Email не указан"}</div>
                    </div>
                </div>
                <div className="badge-row">
                    <StatusBadge status={item.status} />
                </div>
                <div className="meta-row">
                    <span className="meta">Телефон: {item.phoneNumber || "-"}</span>
                </div>
                {canManageEmployers ? (
                    <div className="action-row">
                        <button className="button button-secondary" onClick={() => onOpenEdit({
                            id: item.id,
                            firstName: item.firstName,
                            lastName: item.lastName,
                            email: item.email,
                            phoneNumber: item.phoneNumber,
                            companyId: item.companyId
                        })}>Изменить</button>
                        <button className="button button-accent" onClick={() => onPatch("employers", item.id, "block")}>Блокировать</button>
                        <button className="button button-danger" onClick={() => onDelete("employers", item.id)}>Удалить</button>
                    </div>
                ) : null}
            </article>
        );
    }

    const canManageApplications = auth?.role === "EMPLOYER";
    const canDeleteApplication = auth?.role === "EMPLOYER" || mode === "seeker";

    return (
        <article className="card">
            <div className="card-top">
                <div>
                    <h3 className="card-title">{item.vacancyTitle || "Вакансия"}</h3>
                    <div className="card-subtitle">{item.userFullName || "Неизвестный кандидат"} | {item.resumeTitle || "Без резюме"}</div>
                </div>
            </div>
            <div className="badge-row">
                <StatusBadge status={item.status} />
            </div>
            <div className="card-subtitle">{truncateText(item.coverLetter || "Сопроводительное письмо отсутствует.", 180)}</div>
            <div className="action-row">
                {canManageApplications ? (
                    <button className="button button-secondary" onClick={() => onOpenCopy({
                        userId: item.userId,
                        vacancyId: item.vacancyId,
                        resumeId: item.resumeId,
                        coverLetter: item.coverLetter
                    })}>Создать похожий</button>
                ) : null}
                {canManageApplications ? (
                    <button className="button button-accent" onClick={() => onPatch("applications", item.id, "status?status=ACCEPTED")}>Принять</button>
                ) : null}
                {canDeleteApplication ? (
                    <button className="button button-danger" onClick={() => onDelete("applications", item.id)}>Удалить</button>
                ) : null}
            </div>
        </article>
    );
}

function DetailPanel({ route, detail }) {
    if (route === "companies") {
        return (
            <section className="detail-panel">
                <h3>Связи компании</h3>
                <div className="detail-grid">
                    <div className="detail-card">
                        <div className="detail-title">Работодатели</div>
                        {detail.employers.length
                            ? detail.employers.map((item) => (
                                <div className="detail-subtitle" key={item.id}>{item.firstName} {item.lastName} | {item.email}</div>
                            ))
                            : <div className="detail-subtitle">Связанные работодатели отсутствуют.</div>}
                    </div>
                    <div className="detail-card">
                        <div className="detail-title">Вакансии</div>
                        {detail.vacancies.length
                            ? detail.vacancies.map((item) => (
                                <div className="detail-subtitle" key={item.id}>{item.title} | {formatMoney(item.salary)}</div>
                            ))
                            : <div className="detail-subtitle">Связанные вакансии отсутствуют.</div>}
                    </div>
                </div>
            </section>
        );
    }

    if (route === "users") {
        return (
            <section className="detail-panel">
                <h3>Связи кандидата</h3>
                <div className="detail-grid">
                    <div className="detail-card">
                        <div className="detail-title">Резюме</div>
                        {detail.resumes.length
                            ? detail.resumes.map((item) => (
                                <div className="detail-subtitle" key={item.id}>{item.title} | {formatMoney(item.expectedSalary)}</div>
                            ))
                            : <div className="detail-subtitle">Резюме отсутствуют.</div>}
                    </div>
                    <div className="detail-card">
                        <div className="detail-title">Навыки</div>
                        <div className="tag-row">
                            {detail.skills.length
                                ? detail.skills.map((item) => <span className="tag" key={item.id}>{item.name}</span>)
                                : <span className="tag">Навыки отсутствуют</span>}
                        </div>
                    </div>
                </div>
            </section>
        );
    }

    return (
        <section className="detail-panel">
            <h3>Связи вакансии</h3>
            <div className="detail-card">
                <div className="detail-title">Отклики</div>
                {detail.applications.length
                    ? detail.applications.map((item) => (
                        <div className="detail-subtitle" key={item.id}>
                            {item.userFullName || "Неизвестно"} | {item.status || "PENDING"} | {item.resumeTitle || "Без резюме"}
                        </div>
                    ))
                    : <div className="detail-subtitle">Отклики отсутствуют.</div>}
            </div>
        </section>
    );
}

function FormModal({ modal, auth, entityState, onClose, onSubmit }) {
    const config = forms[modal.entity];
    const initialValues = Object.fromEntries(config.fields.map((item) => [item.name, modal.item?.[item.name] ?? ""]));
    const [values, setValues] = useState(initialValues);
    const isApplicationCreate = modal.entity === "applications" && modal.mode !== "edit";
    const isResumeForm = modal.entity === "resumes";
    const isVacancyForm = modal.entity === "vacancies";
    const isEmployerForm = modal.entity === "employers";
    const userOptions = entityState?.users?.items || [];
    const vacancyOptions = entityState?.vacancies?.items || [];
    const resumeOptions = (entityState?.resumes?.items || [])
        .filter((resume) => !auth?.userId || String(resume.userId) === String(auth.userId));
    const companyOptions = entityState?.companies?.items || [];
    const selectedResume = resumeOptions.find((item) => String(item.id) === String(values.resumeId));
    const resolvedUserId = auth?.role === "SEEKER" && auth?.userId ? String(auth.userId) : values.userId;
    const resolvedEmployerId = auth?.role === "EMPLOYER" && auth?.employerId ? String(auth.employerId) : values.createdById;
    const resolvedCompanyId = auth?.role === "EMPLOYER" && auth?.companyId ? String(auth.companyId) : values.companyId;

    useEffect(() => {
        setValues({
            ...initialValues,
            vacancyId: initialValues.vacancyId || modal.item?.vacancyId || "",
            userId: auth?.role === "SEEKER" && auth?.userId ? String(auth.userId) : initialValues.userId,
            createdById: auth?.role === "EMPLOYER" && auth?.employerId ? String(auth.employerId) : initialValues.createdById,
            companyId: auth?.role === "EMPLOYER" && auth?.companyId ? String(auth.companyId) : initialValues.companyId
        });
    }, [modal.entity, modal.item?.id, auth?.userId, auth?.employerId, auth?.companyId, auth?.role]);

    return (
        <div className="modal-backdrop">
            <div className="modal">
                <div className="modal-header">
                    <div>
                        <h3 className="modal-title">{modal.mode === "edit" ? "Редактировать" : "Создать"}: {config.title}</h3>
                    </div>
                    <button className="button button-secondary" type="button" onClick={onClose}>Закрыть</button>
                </div>
                <form
                    className="modal-body"
                    onSubmit={(event) => {
                        event.preventDefault();
                        onSubmit({
                            ...values,
                            userId: resolvedUserId,
                            createdById: resolvedEmployerId,
                            companyId: resolvedCompanyId
                        });
                    }}>
                    {isApplicationCreate ? (
                        <div className="modal-grid">
                            <div className="field full-span">
                                <label>Вакансия *</label>
                                <select
                                    className="select"
                                    required
                                    value={values.vacancyId}
                                    onChange={(event) => setValues((current) => ({ ...current, vacancyId: event.target.value }))}>
                                    <option value="">Выберите вакансию</option>
                                    {vacancyOptions.map((vacancy) => (
                                        <option key={vacancy.id} value={vacancy.id}>
                                            {vacancy.title} | {vacancy.companyName || "Компания"} | {vacancy.location || "Без локации"}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="field full-span">
                                <label>Резюме *</label>
                                <select
                                    className="select"
                                    required
                                    value={values.resumeId}
                                    onChange={(event) => setValues((current) => ({ ...current, resumeId: event.target.value }))}>
                                    <option value="">Выберите резюме</option>
                                    {resumeOptions.map((resume) => (
                                        <option key={resume.id} value={resume.id}>
                                            {resume.title} | {resume.userFullName || "Кандидат"} | {resume.location || "Без локации"}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            {selectedResume ? (
                                <div className="field full-span">
                                    <label>Кандидат</label>
                                    <input
                                        className="input"
                                        type="text"
                                        value={selectedResume.userFullName || `Пользователь #${selectedResume.userId}`}
                                        readOnly
                                    />
                                </div>
                            ) : null}
                            <div className="field full-span">
                                <label>Сопроводительное письмо</label>
                                <textarea
                                    className="textarea"
                                    value={values.coverLetter}
                                    onChange={(event) => setValues((current) => ({ ...current, coverLetter: event.target.value }))}
                                />
                            </div>
                        </div>
                    ) : isResumeForm ? (
                        <div className="modal-grid">
                            {auth?.role === "SEEKER" && auth?.userId ? null : (
                            <div className="field full-span">
                                <label>Кандидат *</label>
                                <select
                                    className="select"
                                    required
                                    value={values.userId}
                                    onChange={(event) => setValues((current) => ({ ...current, userId: event.target.value }))}>
                                    <option value="">Выберите кандидата</option>
                                    {userOptions.map((user) => (
                                        <option key={user.id} value={user.id}>
                                            {`${user.firstName || ""} ${user.lastName || ""}`.trim()} | {user.email || "Без email"}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            )}
                            {config.fields
                                .filter((formField) => formField.name !== "userId")
                                .map((formField) => (
                                    <div className={`field ${formField.fullSpan ? "full-span" : ""}`} key={formField.name}>
                                        <label>{formField.label}{formField.required ? " *" : ""}</label>
                                        {formField.type === "textarea" ? (
                                            <textarea
                                                className="textarea"
                                                required={formField.required}
                                                value={values[formField.name]}
                                                onChange={(event) => setValues((current) => ({ ...current, [formField.name]: event.target.value }))}
                                            />
                                        ) : (
                                            <input
                                                className="input"
                                                type={formField.type}
                                                required={formField.required}
                                                value={values[formField.name]}
                                                onChange={(event) => setValues((current) => ({ ...current, [formField.name]: event.target.value }))}
                                            />
                                        )}
                                    </div>
                                ))}
                        </div>
                    ) : (
                        <div className="modal-grid">
                            {config.fields.map((formField) => (
                                <div className={`field ${formField.fullSpan ? "full-span" : ""}`} key={formField.name}>
                                    <label>{formField.label}{formField.required ? " *" : ""}</label>
                                    {formField.type === "textarea" ? (
                                        <textarea
                                            className="textarea"
                                            required={formField.required}
                                            value={values[formField.name]}
                                            onChange={(event) => setValues((current) => ({ ...current, [formField.name]: event.target.value }))}
                                        />
                                    ) : (
                                        <input
                                            className="input"
                                            type={formField.type}
                                            required={formField.required}
                                            value={values[formField.name]}
                                            onChange={(event) => setValues((current) => ({ ...current, [formField.name]: event.target.value }))}
                                        />
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                    <div className="button-row">
                        <button className="button button-primary" type="submit">{modal.mode === "edit" ? "Сохранить" : "Создать"}</button>
                        <button className="button button-secondary" type="button" onClick={onClose}>Отмена</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function AuthModal({ authModal, companies, onCreateCompany, onClose, onSubmit }) {
    const [kind, setKind] = useState(authModal.kind || "login");
    const isRegister = kind === "register";
    const initialValues = {
        login: "",
        password: "",
        role: authModal.role || "SEEKER",
        firstName: "",
        lastName: "",
        email: "",
        phoneNumber: "",
        companyId: ""
    };
    const initialCompanyValues = {
        name: "",
        industry: "",
        location: "",
        website: "",
        contactEmail: "",
        contactPhone: "",
        description: ""
    };
    const [values, setValues] = useState(initialValues);
    const [creatingCompany, setCreatingCompany] = useState(false);
    const [companyValues, setCompanyValues] = useState(initialCompanyValues);
    const [companyError, setCompanyError] = useState("");

    useEffect(() => {
        setValues((current) => ({
            ...initialValues,
            role: authModal.role || current.role || "SEEKER"
        }));
        setCreatingCompany(false);
        setCompanyValues(initialCompanyValues);
        setCompanyError("");
        setKind(authModal.kind || "login");
    }, [authModal.kind, authModal.role]);

    async function handleCreateCompany() {
        try {
            setCompanyError("");
            const createdCompany = await onCreateCompany(companyValues);
            setValues((current) => ({ ...current, companyId: String(createdCompany.id) }));
            setCompanyValues(initialCompanyValues);
            setCreatingCompany(false);
        } catch (requestError) {
            setCompanyError(requestError.message);
        }
    }

    return (
        <div className="modal-backdrop">
            <div className="modal auth-modal">
                <div className="modal-header">
                    <div>
                        <h3 className="modal-title">{isRegister ? "Регистрация" : "Вход"}</h3>
                    </div>
                    <button className="button button-secondary" type="button" onClick={onClose}>Закрыть</button>
                </div>
                <form
                    className="modal-body"
                    onSubmit={(event) => {
                        event.preventDefault();
                        onSubmit(cleanAuthPayload(values, isRegister), kind);
                    }}>
                    <div className="auth-switch">
                        <button
                            className={`auth-switch-button ${kind === "login" ? "active" : ""}`}
                            type="button"
                            onClick={() => setKind("login")}>
                            Вход
                        </button>
                        <button
                            className={`auth-switch-button ${kind === "register" ? "active" : ""}`}
                            type="button"
                            onClick={() => setKind("register")}>
                            Регистрация
                        </button>
                    </div>
                    <div className="modal-grid">
                        <div className="field full-span">
                            <label>Роль *</label>
                            <div className="role-choice">
                                <button
                                    className={`role-choice-button ${values.role === "SEEKER" ? "active" : ""}`}
                                    type="button"
                                    onClick={() => setValues((current) => ({ ...current, role: "SEEKER" }))}>
                                    Ищу вакансию
                                </button>
                                <button
                                    className={`role-choice-button ${values.role === "EMPLOYER" ? "active" : ""}`}
                                    type="button"
                                    onClick={() => setValues((current) => ({ ...current, role: "EMPLOYER" }))}>
                                    Ищу сотрудника
                                </button>
                            </div>
                        </div>
                        <div className="field">
                            <label>Логин *</label>
                            <input
                                className="input"
                                type="text"
                                required
                                value={values.login}
                                onChange={(event) => setValues((current) => ({ ...current, login: event.target.value }))}
                            />
                        </div>
                        <div className="field">
                            <label>Пароль *</label>
                            <input
                                className="input"
                                type="password"
                                required
                                value={values.password}
                                onChange={(event) => setValues((current) => ({ ...current, password: event.target.value }))}
                            />
                        </div>
                        {isRegister ? (
                            <>
                                <div className="field">
                                    <label>Имя *</label>
                                    <input
                                        className="input"
                                        type="text"
                                        required
                                        value={values.firstName}
                                        onChange={(event) => setValues((current) => ({ ...current, firstName: event.target.value }))}
                                    />
                                </div>
                                <div className="field">
                                    <label>Фамилия *</label>
                                    <input
                                        className="input"
                                        type="text"
                                        required
                                        value={values.lastName}
                                        onChange={(event) => setValues((current) => ({ ...current, lastName: event.target.value }))}
                                    />
                                </div>
                                <div className="field">
                                    <label>Email *</label>
                                    <input
                                        className="input"
                                        type="email"
                                        required
                                        value={values.email}
                                        onChange={(event) => setValues((current) => ({ ...current, email: event.target.value }))}
                                    />
                                </div>
                                <div className="field">
                                    <label>Телефон</label>
                                    <input
                                        className="input"
                                        type="text"
                                        value={values.phoneNumber}
                                        onChange={(event) => setValues((current) => ({ ...current, phoneNumber: event.target.value }))}
                                    />
                                </div>
                                {values.role === "EMPLOYER" ? (
                                    <div className="field full-span">
                                        <label>Компания *</label>
                                        <select
                                            className="select"
                                            required
                                            value={values.companyId}
                                            onChange={(event) => setValues((current) => ({ ...current, companyId: event.target.value }))}>
                                            <option value="">Выберите компанию</option>
                                            {companies.map((company) => (
                                                <option key={company.id} value={company.id}>{company.name}</option>
                                            ))}
                                        </select>
                                        <div className="button-row">
                                            <button
                                                className="button button-secondary"
                                                type="button"
                                                onClick={() => {
                                                    setCreatingCompany((current) => !current);
                                                    setCompanyError("");
                                                }}>
                                                {creatingCompany ? "Скрыть форму компании" : "Добавить новую компанию"}
                                            </button>
                                        </div>
                                    </div>
                                ) : null}
                                {values.role === "EMPLOYER" && creatingCompany ? (
                                    <>
                                        <div className="field">
                                            <label>Название компании *</label>
                                            <input
                                                className="input"
                                                type="text"
                                                value={companyValues.name}
                                                onChange={(event) => setCompanyValues((current) => ({ ...current, name: event.target.value }))}
                                            />
                                        </div>
                                        <div className="field">
                                            <label>Отрасль</label>
                                            <input
                                                className="input"
                                                type="text"
                                                value={companyValues.industry}
                                                onChange={(event) => setCompanyValues((current) => ({ ...current, industry: event.target.value }))}
                                            />
                                        </div>
                                        <div className="field">
                                            <label>Локация</label>
                                            <input
                                                className="input"
                                                type="text"
                                                value={companyValues.location}
                                                onChange={(event) => setCompanyValues((current) => ({ ...current, location: event.target.value }))}
                                            />
                                        </div>
                                        <div className="field">
                                            <label>Сайт</label>
                                            <input
                                                className="input"
                                                type="text"
                                                value={companyValues.website}
                                                onChange={(event) => setCompanyValues((current) => ({ ...current, website: event.target.value }))}
                                            />
                                        </div>
                                        <div className="field">
                                            <label>Email компании</label>
                                            <input
                                                className="input"
                                                type="email"
                                                value={companyValues.contactEmail}
                                                onChange={(event) => setCompanyValues((current) => ({ ...current, contactEmail: event.target.value }))}
                                            />
                                        </div>
                                        <div className="field">
                                            <label>Телефон компании</label>
                                            <input
                                                className="input"
                                                type="text"
                                                value={companyValues.contactPhone}
                                                onChange={(event) => setCompanyValues((current) => ({ ...current, contactPhone: event.target.value }))}
                                            />
                                        </div>
                                        <div className="field full-span">
                                            <label>Описание</label>
                                            <textarea
                                                className="textarea"
                                                value={companyValues.description}
                                                onChange={(event) => setCompanyValues((current) => ({ ...current, description: event.target.value }))}
                                            />
                                        </div>
                                        {companyError ? (
                                            <div className="field full-span">
                                                <div className="notice notice-error">{companyError}</div>
                                            </div>
                                        ) : null}
                                        <div className="field full-span">
                                            <div className="button-row">
                                                <button className="button button-primary" type="button" onClick={handleCreateCompany}>
                                                    Создать компанию
                                                </button>
                                            </div>
                                        </div>
                                    </>
                                ) : null}
                            </>
                        ) : null}
                    </div>
                    <div className="button-row">
                        <button className="button button-primary" type="submit">{isRegister ? "Зарегистрироваться" : "Войти"}</button>
                        <button className="button button-secondary" type="button" onClick={onClose}>Отмена</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function DetailModal({ detailModal, onClose }) {
    return (
        <div className="modal-backdrop">
            <div className="modal">
                <div className="modal-header">
                    <div>
                        <h3 className="modal-title">{detailModal.title}</h3>
                    </div>
                    <button className="button button-secondary" type="button" onClick={onClose}>Закрыть</button>
                </div>
                <div className="modal-body">
                    <DetailPanel route={detailModal.route} detail={detailModal.detail} />
                </div>
            </div>
        </div>
    );
}

function Notices({ error, success, loading }) {
    return (
        <>
            {loading ? <div className="notice notice-info"><span className="loading">Загрузка</span></div> : null}
            {error ? <div className="notice notice-error">{error}</div> : null}
            {success ? <div className="notice notice-success">{success}</div> : null}
        </>
    );
}

function StatusBadge({ status }) {
    const normalized = String(status || "UNKNOWN").toUpperCase();
    const classes = {
        ACTIVE: "badge-success",
        ACCEPTED: "badge-success",
        PENDING: "badge-warning",
        CLOSED: "badge-danger",
        BLOCKED: "badge-danger",
        HIDDEN: "badge-danger",
        CANCELLED: "badge-danger"
    };
    return <span className={`badge ${classes[normalized] || ""}`}>{normalized}</span>;
}

function StatCard({ label, value }) {
    return (
        <div className="stat-card">
            <div className="stat-card-label">{label}</div>
            <div className="stat-card-value">{value}</div>
        </div>
    );
}

async function fetchEntityItems(entity, filters, context = {}) {
    const { auth, mode } = context;

    if (entity === "vacancies") {
        const items = await requestList("/api/vacancies");
        return items.filter((item) => matchesVacancyFilters(item, filters));
    }

    if (entity === "companies") {
        if (filters.keyword) {
            return requestList(`/api/companies/search${toQueryString({ keyword: filters.keyword })}`);
        }
        return requestList(`/api/companies${toQueryString(cleanParams({ industry: filters.industry }))}`);
    }

    if (entity === "users") {
        return requestList(`/api/users${toQueryString(cleanParams(filters))}`);
    }

    if (entity === "resumes") {
        if (mode === "seeker" && auth?.userId && context.resumeView === "mine") {
            const items = await requestList(`/api/resumes/user/${auth.userId}`);
            return items.filter((item) => matchesResumeFilters(item, filters));
        }
        const items = await requestList("/api/resumes");
        return items.filter((item) => matchesResumeFilters(item, filters));
    }

    if (entity === "employers") {
        if (filters.email) {
            return [await request(`/api/employers/by-email${toQueryString({ email: filters.email })}`)];
        }
        if (filters.companyId) {
            return requestList(`/api/employers/company/${filters.companyId}`);
        }
        return requestList("/api/employers");
    }

    if (entity === "applications" && mode === "seeker" && auth?.userId) {
        const items = await requestList(`/api/applications/user/${auth.userId}`);
        return items.filter((item) => matchesApplicationFilters(item, filters));
    }

    if (entity === "applications" && mode === "employer" && auth?.role === "EMPLOYER" && auth?.companyId) {
        const vacancies = await requestList(`/api/vacancies${toQueryString({ companyId: auth.companyId })}`);
        if (vacancies.length === 0) {
            return [];
        }

        const applicationGroups = await Promise.all(
            vacancies.map((vacancy) => requestList(`/api/applications/vacancy/${vacancy.id}`))
        );

        return applicationGroups
            .flat()
            .filter((item) => matchesApplicationFilters(item, filters));
    }

    const hasSearchFilters = Object.values(filters).some((value) => value !== "");
    if (hasSearchFilters) {
        return requestList(`/api/applications/search/jpql${toQueryString(cleanParams(filters))}`);
    }
    const page = await request("/api/applications?page=0&size=30&sort=id,desc");
    return page.content || [];
}

function collectPayload(values, fields) {
    const payload = {};
    fields.forEach((item) => {
        const value = values[item.name];
        if (value === "" || value === null || value === undefined) {
            return;
        }
        payload[item.name] = item.type === "number" ? Number(value) : value;
    });
    return payload;
}

async function requestList(url) {
    const result = await request(url);
    return Array.isArray(result) ? result : [];
}

function getStoredAuth() {
    try {
        const raw = window.localStorage.getItem(AUTH_STORAGE_KEY);
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
}

function persistAuth(auth) {
    window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth));
}

function clearStoredAuth() {
    window.localStorage.removeItem(AUTH_STORAGE_KEY);
}

async function request(url, options = {}) {
    const storedAuth = getStoredAuth();
    const response = await fetch(url, {
        headers: {
            "Content-Type": "application/json",
            ...(storedAuth?.token ? { Authorization: `Bearer ${storedAuth.token}` } : {}),
            ...(options.headers || {})
        },
        ...options
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    let data = null;
    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }
    }

    if (!response.ok) {
        if (response.status === 401) {
            clearStoredAuth();
        }
        throw new Error(
            typeof data === "object" && data !== null
                ? data.message || `Request failed with status ${response.status}`
                : `Request failed with status ${response.status}`
        );
    }

    return normalizeApiPayload(data);
}

function getRouteFromHash() {
    const hash = window.location.hash.replace("#", "");
    return hash && hash in routeLabels ? hash : "home";
}

function getVisibleRoutes(mode, auth) {
    const routes = [...modeConfigs[mode].routes];

    if (mode === "employer" && auth?.role !== "EMPLOYER") {
        return routes.filter((route) => route !== "users");
    }

    return routes;
}

function field(name, label, type, required = false, fullSpan = false) {
    return { name, label, type, required, fullSpan };
}

function filterField(name, label, type) {
    return { name, label, type };
}

function getRequiredRole(entity, action) {
    if (!entity) {
        return null;
    }

    if (entity === "applications") {
        if (action === "create") {
            return "SEEKER";
        }
        if (action === "status") {
            return "EMPLOYER";
        }
        return null;
    }

    if (entity === "resumes" || entity === "users") {
        return "SEEKER";
    }

    if (entity === "companies" || entity === "employers" || entity === "vacancies") {
        return "EMPLOYER";
    }

    return null;
}

function cleanAuthPayload(values, isRegister) {
    const payload = {
        login: values.login.trim(),
        password: values.password,
        role: values.role
    };

    if (!isRegister) {
        return payload;
    }

    return cleanParams({
        ...payload,
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        email: values.email.trim(),
        phoneNumber: values.phoneNumber.trim(),
        companyId: values.companyId ? Number(values.companyId) : null
    });
}

function cleanParams(obj) {
    return Object.fromEntries(
        Object.entries(obj).filter(([, value]) => value !== "" && value !== null && value !== undefined)
    );
}

function getDisplayedItems(entity, state) {
    const items = Array.isArray(state?.items) ? state.items : [];
    const filters = state?.filters || {};

    if (entity === "vacancies") {
        return items.filter((item) => matchesVacancyFilters(item, filters));
    }

    if (entity === "resumes") {
        return items.filter((item) => matchesResumeFilters(item, filters));
    }

    return items;
}

function matchesVacancyFilters(item, filters) {
    const titleTokens = tokenizeSearch(filters.title);
    const locationNeedle = normalizeSearch(filters.location);
    const minSalary = filters.minSalary === "" ? null : Number(filters.minSalary);
    const maxExperience = filters.maxExperience === "" ? null : Number(filters.maxExperience);
    const companyId = filters.companyId === "" ? null : String(filters.companyId);

    if (titleTokens.length > 0 && !containsEveryToken([item.title, item.description, item.companyName], titleTokens)) {
        return false;
    }
    if (locationNeedle && !containsValue([item.location], locationNeedle)) {
        return false;
    }
    if (minSalary !== null && Number(item.salary || 0) < minSalary) {
        return false;
    }
    if (maxExperience !== null && Number(item.requiredExperience || 0) > maxExperience) {
        return false;
    }
    if (companyId && item.companyId !== undefined && item.companyId !== null && String(item.companyId) !== companyId) {
        return false;
    }
    return true;
}

function matchesResumeFilters(item, filters) {
    const skillTokens = tokenizeSearch(filters.skill);
    const locationNeedle = normalizeSearch(filters.location);
    const maxSalary = filters.maxSalary === "" ? null : Number(filters.maxSalary);

    if (skillTokens.length > 0 && !containsEveryToken([item.title, item.skills, item.userFullName], skillTokens)) {
        return false;
    }
    if (locationNeedle && !containsValue([item.location], locationNeedle)) {
        return false;
    }
    if (maxSalary !== null && Number(item.expectedSalary || 0) > maxSalary) {
        return false;
    }
    return true;
}

function matchesApplicationFilters(item, filters) {
    const statusNeedle = normalizeSearch(filters.status);
    const vacancyTokens = tokenizeSearch(filters.vacancyTitle);
    const resumeTokens = tokenizeSearch(filters.resumeTitle);

    if (statusNeedle && normalizeSearch(item.status) !== statusNeedle) {
        return false;
    }
    if (vacancyTokens.length > 0 && !containsEveryToken([item.vacancyTitle], vacancyTokens)) {
        return false;
    }
    if (resumeTokens.length > 0 && !containsEveryToken([item.resumeTitle], resumeTokens)) {
        return false;
    }
    return true;
}

function containsValue(values, needle) {
    return values.some((value) => normalizeSearch(value).includes(needle));
}

function containsEveryToken(values, tokens) {
    const haystack = values.map((value) => normalizeSearch(value)).join(" ");
    return tokens.every((token) => haystack.includes(token));
}

function normalizeSearch(value) {
    return String(value || "").trim().toLowerCase();
}

function tokenizeSearch(value) {
    return normalizeSearch(value)
        .split(/\s+/)
        .filter(Boolean);
}

function normalizeApiPayload(value) {
    if (Array.isArray(value)) {
        return value.map(normalizeApiPayload);
    }

    if (value && typeof value === "object") {
        return Object.fromEntries(
            Object.entries(value).map(([key, nestedValue]) => [key, normalizeApiPayload(nestedValue)])
        );
    }

    if (typeof value === "string") {
        return repairMojibake(value);
    }

    return value;
}

function repairMojibake(value) {
    if (typeof value !== "string" || !value.trim()) {
        return value;
    }

    let current = value;

    for (let i = 0; i < 2; i += 1) {
        const decoded = tryDecodeCp1251Utf8(current);
        if (!decoded || decoded === current) {
            break;
        }

        if (getReadabilityScore(decoded) > getReadabilityScore(current)) {
            current = decoded;
            continue;
        }

        break;
    }

    return current;
}

function looksLikeMojibake(value) {
    return /(?:Ð.|Ñ.|Р.|С.)/.test(value);
}

function tryDecodeCp1251Utf8(value) {
    try {
        const bytes = Uint8Array.from(Array.from(value, (char) => char.charCodeAt(0) & 0xff));
        return new TextDecoder("utf-8").decode(bytes);
    } catch {
        return value;
    }
}

function getReadabilityScore(value) {
    const cyrillic = (value.match(/[\u0400-\u04FF\u0500-\u052F\u2DE0-\u2DFF\uA640-\uA69F]/g) || []).length;
    const latin = (value.match(/[A-Za-z]/g) || []).length;
    const digits = (value.match(/\d/g) || []).length;
    const spaces = (value.match(/\s/g) || []).length;
    const punctuation = (value.match(/[.,:;!?()[\]{}\-"'`/@#$%^&*_+=<>|\\]/g) || []).length;
    const replacement = (value.match(/\uFFFD/g) || []).length;
    const mojibakeMarkers = (value.match(/[\u0080-\u009F\u00A0-\u00BF]/g) || []).length;
    const brokenPairs = (value.match(/[РС][^\s\u0400-\u04FF]/g) || []).length;

    return (cyrillic * 4) + latin + digits + spaces + punctuation - (replacement * 20) - (mojibakeMarkers * 8) - (brokenPairs * 6);
}

function repairRenderedContent(root) {
    if (!root) {
        return;
    }

    const walker = document.createTreeWalker(root, NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT);
    let current = walker.currentNode;

    while (current) {
        if (current.nodeType === Node.TEXT_NODE) {
            current.textContent = repairMojibake(current.textContent);
        } else if (current.nodeType === Node.ELEMENT_NODE) {
            if (current.hasAttribute?.("placeholder")) {
                current.setAttribute("placeholder", repairMojibake(current.getAttribute("placeholder")));
            }
            if (current.tagName === "INPUT" && current.type !== "password" && current.type !== "hidden" && current.value) {
                current.value = repairMojibake(current.value);
            }
        }

        current = walker.nextNode();
    }
}

function toQueryString(params) {
    const search = new URLSearchParams(params);
    const query = search.toString();
    return query ? `?${query}` : "";
}

function formatMoney(value) {
    if (value === null || value === undefined || value === "") {
        return "-";
    }
    return `${new Intl.NumberFormat("ru-RU").format(value)} BYN`;
}

function truncateText(text, limit) {
    const normalized = String(text || "");
    return normalized.length > limit ? `${normalized.slice(0, limit - 1)}...` : normalized;
}

ReactDOM.createRoot(document.getElementById("app")).render(<App />);
