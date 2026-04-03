import { ref } from 'vue'

export function useRouterInterceptor({ sisome, dataLoader, failureHandler, injectOption }) {
    let loaded = false

    return {
        async filter(to, from, next) {
            if (loaded) {
                next()
                return
            }
            try {
                const result = await dataLoader(to, from, next)
                if (result.data && result.data.length > 0) {
                    injectRoutes(result.data, sisome, injectOption)
                }
                loaded = true
                next({ ...to, replace: true })
            } catch(e) {
                failureHandler && failureHandler({ msg: e.message }, to, from, next)
            }
        },
        reset() {
            loaded = false
        }
    }
}

function injectRoutes(menus, sisome, options) {
    if (!options || !options.parentRoute) return
}

export function useRouterAlive({ sisome, router, element, placeHolderComponent }) {
    const RouteAlive = {
        name: 'RouteAlive',
        setup() {
            return () => null
        }
    }

    const removeComponentCache = (name) => {}

    return { RouteAlive, removeComponentCache }
}