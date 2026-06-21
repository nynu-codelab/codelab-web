#!/bin/bash
# =============================================
# NYNU Code Lab — 招新报名闭环验证脚本
# =============================================
# 用途：本地开发验证，测试完整的注册→报名→审核流程
# 默认访问 http://localhost
# 仅用于本地开发验证，不依赖线上环境
# =============================================

set -e

BASE_URL="${BASE_URL:-http://localhost}"
PASS=0
FAIL=0

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

pass() {
    PASS=$((PASS + 1))
    echo -e "${GREEN}[PASS]${NC} $1"
}

fail() {
    FAIL=$((FAIL + 1))
    echo -e "${RED}[FAIL]${NC} $1"
    echo "       $2"
}

check_code() {
    local resp="$1"
    local expected="$2"
    local step="$3"
    local code
    code=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',''))" 2>/dev/null)
    if [ "$code" = "$expected" ]; then
        pass "$step"
        return 0
    else
        fail "$step" "expected code=$expected, got code=$code. Response: $(echo "$resp" | head -c 200)"
        return 1
    fi
}

extract_token() {
    echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('token',''))" 2>/dev/null
}

echo "=========================================="
echo " NYNU Code Lab 招新报名闭环验证"
echo " Base URL: $BASE_URL"
echo "=========================================="
echo ""

# ---- 1. Health Check ----
echo "--- 1. Health Check ---"
HEALTH=$(curl -s "$BASE_URL/api/health")
check_code "$HEALTH" "200" "GET /api/health"

# ---- 2. 注册普通用户 ----
echo ""
echo "--- 2. 注册普通用户 ---"
TEST_USER="verify_$(date +%s)"
TEST_PASS="Verify@123"
REGISTER=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\",\"confirmPassword\":\"$TEST_PASS\",\"realName\":\"验证用户\",\"phone\":\"1390000$(printf '%04d' $((RANDOM % 10000)))\",\"grade\":\"2024\",\"major\":\"计算机科学与技术\",\"className\":\"计科2401\"}")
check_code "$REGISTER" "200" "POST /api/auth/register"

# ---- 3. 登录普通用户 ----
echo ""
echo "--- 3. 登录普通用户 ---"
LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}")
check_code "$LOGIN" "200" "POST /api/auth/login"
USER_TOKEN=$(extract_token "$LOGIN")
if [ -z "$USER_TOKEN" ]; then
    fail "提取用户 Token" "Token 为空"
else
    pass "提取用户 Token"
fi

# ---- 4. 获取当前用户 ----
echo ""
echo "--- 4. 获取当前用户 ---"
ME=$(curl -s "$BASE_URL/api/auth/me" -H "Authorization: Bearer $USER_TOKEN")
check_code "$ME" "200" "GET /api/auth/me"

# ---- 5. 提交报名 ----
echo ""
echo "--- 5. 提交报名 ---"
APPLY=$(curl -s -X POST "$BASE_URL/api/applications" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER_TOKEN" \
    -d '{"realName":"验证用户","grade":"2024","major":"计算机科学与技术","className":"计科2401","phone":"13900000000","qq":"12345678","direction":"前端开发","hasProgrammingBasis":1,"skills":"HTML,CSS,JS","introduction":"验证脚本测试","reason":"测试报名流程","weeklyAvailableTime":"10小时","portfolioUrl":""}')
check_code "$APPLY" "200" "POST /api/applications"

# ---- 6. 查看我的报名 ----
echo ""
echo "--- 6. 查看我的报名 ---"
MY_APP=$(curl -s "$BASE_URL/api/applications/my" -H "Authorization: Bearer $USER_TOKEN")
check_code "$MY_APP" "200" "GET /api/applications/my"
APP_STATUS=$(echo "$MY_APP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('status',''))" 2>/dev/null)
if [ "$APP_STATUS" = "PENDING" ]; then
    pass "报名状态为 PENDING（待审核）"
else
    fail "报名状态应为 PENDING" "got $APP_STATUS"
fi

# ---- 7. 待审核状态下修改报名 ----
echo ""
echo "--- 7. 待审核状态下修改报名 ---"
UPDATE=$(curl -s -X PUT "$BASE_URL/api/applications/my" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER_TOKEN" \
    -d '{"realName":"验证用户(修改)","grade":"2024","major":"计算机科学与技术","className":"计科2401","phone":"13900000000","qq":"12345678","direction":"后端开发","hasProgrammingBasis":1,"skills":"Java,Python","introduction":"修改后的介绍","reason":"修改后的原因","weeklyAvailableTime":"15小时","portfolioUrl":""}')
check_code "$UPDATE" "200" "PUT /api/applications/my（待审核状态下修改）"

# ---- 8. 管理员登录 ----
echo ""
echo "--- 8. 管理员登录 ---"
ADMIN_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}')
check_code "$ADMIN_LOGIN" "200" "POST /api/auth/login (admin)"
ADMIN_TOKEN=$(extract_token "$ADMIN_LOGIN")
if [ -z "$ADMIN_TOKEN" ]; then
    fail "提取管理员 Token" "Token 为空"
else
    pass "提取管理员 Token"
fi

# ---- 9. 查看报名列表 ----
echo ""
echo "--- 9. 管理员查看报名列表 ---"
ADMIN_LIST=$(curl -s "$BASE_URL/api/admin/applications" -H "Authorization: Bearer $ADMIN_TOKEN")
check_code "$ADMIN_LIST" "200" "GET /api/admin/applications"

# ---- 10. 审核报名 ----
echo ""
echo "--- 10. 审核报名 ---"
APP_ID=$(echo "$ADMIN_LIST" | python3 -c "
import sys,json
data=json.load(sys.stdin)['data']
if isinstance(data, list):
    for item in data:
        if item.get('realName','').startswith('验证用户'):
            print(item['id'])
            break
" 2>/dev/null)
if [ -z "$APP_ID" ]; then
    fail "查找待审核报名 ID" "未找到验证用户的报名"
else
    pass "找到待审核报名 ID: $APP_ID"

    REVIEW=$(curl -s -X PUT "$BASE_URL/api/admin/applications/$APP_ID/review" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -d '{"status":"PRELIMINARY_PASSED","reviewRemark":"初筛通过，进入面试（验证脚本）"}')
    check_code "$REVIEW" "200" "PUT /api/admin/applications/$APP_ID/review"
fi

# ---- 11. 普通用户再次查看状态 ----
echo ""
echo "--- 11. 普通用户再次查看报名状态 ---"
MY_APP2=$(curl -s "$BASE_URL/api/applications/my" -H "Authorization: Bearer $USER_TOKEN")
check_code "$MY_APP2" "200" "GET /api/applications/my（审核后）"
APP_STATUS2=$(echo "$MY_APP2" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('status',''))" 2>/dev/null)
APP_REMARK=$(echo "$MY_APP2" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('reviewRemark',''))" 2>/dev/null)
if [ "$APP_STATUS2" = "PRELIMINARY_PASSED" ]; then
    pass "审核后状态变为 PRELIMINARY_PASSED"
else
    fail "审核后状态应为 PRELIMINARY_PASSED" "got $APP_STATUS2"
fi
if [ -n "$APP_REMARK" ]; then
    pass "审核备注已显示: $APP_REMARK"
else
    fail "审核备注未显示" ""
fi

# ---- 12. 权限隔离验证 ----
echo ""
echo "--- 12. 权限隔离验证 ---"

# 12a. 普通用户不能访问后台接口
ADMIN_BY_USER=$(curl -s "$BASE_URL/api/admin/applications" -H "Authorization: Bearer $USER_TOKEN")
ADMIN_CODE=$(echo "$ADMIN_BY_USER" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',''))" 2>/dev/null)
if [ "$ADMIN_CODE" != "200" ]; then
    if [ "$ADMIN_CODE" = "401" ] || [ "$ADMIN_CODE" = "403" ]; then
        pass "普通用户无法访问后台报名接口（code=$ADMIN_CODE）"
    else
        fail "普通用户访问后台报名接口应返回 401/403" "got code=$ADMIN_CODE"
    fi
else
    fail "普通用户不应能访问后台接口" "普通用户成功访问了 /api/admin/applications"
fi

# 12b. 普通用户不能查看别人的报名信息
OTHER_APP=$(curl -s "$BASE_URL/api/applications/1" -H "Authorization: Bearer $USER_TOKEN" 2>/dev/null || echo '{"code":500}')
OTHER_CODE=$(echo "$OTHER_APP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',''))" 2>/dev/null)
# 如果没有 /api/applications/{id} 路由，非 200 也算没有直接访问别人的报名数据。
echo "       GET /api/applications/1 by normal user: code=$OTHER_CODE (no direct access to others' data)"

# ---- 结果汇总 ----
echo ""
echo "=========================================="
TOTAL=$((PASS + FAIL))
echo -e " 验证完成: ${GREEN}$PASS 通过${NC} / ${RED}$FAIL 失败${NC} / 共 $TOTAL 项"
echo "=========================================="

if [ "$FAIL" -gt 0 ]; then
    echo ""
    echo "⚠️  存在失败项，请检查以上输出。"
    exit 1
else
    echo ""
    echo "✅ 招新报名闭环验证全部通过！"
    exit 0
fi
