import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import MainPage from "../pages/MainPage.vue";
import AuthPage from "../pages/AuthPage.vue";
import {useStorage} from "@vueuse/core";


const routes: RouteRecordRaw[] = [
    {
        path: '/', component: MainPage,
        children: [{
            path: "/",
            component: () => import("../pages/main/HomePage.vue")
        }, {
            path: "/medicineManage",
            component: () => import("../pages/main/MedicineManagePage.vue")
        }, {
            path: "/restock",
            component: () => import("../pages/main/RestockManagePage.vue")
        }
            , {
                path: "/sale",
                component: () => import("../pages/main/PurchasePage.vue")
            }
            , {
                path: "/bigType",
                component: () => import("../pages/main/BigTypePage.vue")
            }
            , {
                path: "/type",
                component: () => import("../pages/main/TypePage.vue")
            }
            , {
                path: "/userManage",
                component: () => import("../pages/main/UserManagePage.vue")
            }
        ]
    },
    {
        path: '/', component: AuthPage,
        children: [
            {
                path: '/login',
                component: () => import("../pages/auth/LoginPage.vue")
            },
            {
                path: '/loginByEmail',
                component: () => import("../pages/auth/LoginByCodePage.vue")
            },
            {
                path: '/register',
                component: () => import("../pages/auth/RegisterPage.vue")
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})
router.beforeEach(async (to, from) => {
    const token =
        useStorage('token', "")
    if (
        token.value !== "" &&
        (to.path === '/login' || to.path === '/register')
    ) {
        return '/'
    }
})
export default router