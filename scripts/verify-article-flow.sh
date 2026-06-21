#!/bin/bash
# =============================================
# NYNU Code Lab — 文章闭环验证脚本
# =============================================
# 用途：本地开发验证，测试完整的文章创建→发布→展示→下架→删除流程
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
NC='\033[0m'

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
        fail "$step" "expected code=$expected, got code=$code"
        return 1
    fi
}

check_code_ne() {
    local resp="$1"
    local not_expected="$2"
    local step="$3"
    local code
    code=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',''))" 2>/dev/null)
    if [ "$code" != "$not_expected" ]; then
        pass "$step (code=$code)"
        return 0
    else
        fail "$step" "expected code != $not_expected, got code=$code"
        return 1
    fi
}

extract_token() {
    echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('token',''))" 2>/dev/null
}

echo "=========================================="
echo " NYNU Code Lab 文章闭环验证"
echo " Base URL: $BASE_URL"
echo "=========================================="
echo ""

# ---- 1. Health Check ----
echo "--- 1. Health Check ---"
HEALTH=$(curl -s "$BASE_URL/api/health")
check_code "$HEALTH" "200" "GET /api/health"

# ---- 2. 管理员登录 ----
echo ""
echo "--- 2. 管理员登录 ---"
ADMIN_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}')
check_code "$ADMIN_LOGIN" "200" "管理员登录"
ADMIN_TOKEN=$(extract_token "$ADMIN_LOGIN")
if [ -z "$ADMIN_TOKEN" ]; then
    fail "提取管理员 Token" "Token 为空"
else
    pass "提取管理员 Token"
fi

# ---- 3. 创建草稿文章 ----
echo ""
echo "--- 3. 创建草稿文章 ---"
CREATE_ARTICLE=$(curl -s -X POST "$BASE_URL/api/admin/articles" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{"title":"验证测试文章","summary":"这是一篇验证测试文章的摘要","coverUrl":"","contentMarkdown":"# 验证测试\n\n这是一篇**验证测试**文章。\n\n## 二级标题\n\n- 列表项1\n- 列表项2\n\n```java\nSystem.out.println(\"Hello\");\n```","category":"测试分类","tags":"[\"测试\",\"验证\"]","sortOrder":0}')
check_code "$CREATE_ARTICLE" "200" "创建草稿文章"
ARTICLE_ID=$(echo "$CREATE_ARTICLE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('id',''))" 2>/dev/null)
if [ -z "$ARTICLE_ID" ]; then
    fail "提取文章 ID" "ID 为空"
else
    pass "提取文章 ID: $ARTICLE_ID"
fi

# ---- 4. 查看后台文章列表 ----
echo ""
echo "--- 4. 后台文章列表 ---"
ADMIN_LIST=$(curl -s "$BASE_URL/api/admin/articles" -H "Authorization: Bearer $ADMIN_TOKEN")
check_code "$ADMIN_LIST" "200" "管理员查看文章列表"

# ---- 5. 查看后台文章详情 ----
echo ""
echo "--- 5. 后台文章详情 ---"
ADMIN_DETAIL=$(curl -s "$BASE_URL/api/admin/articles/$ARTICLE_ID" -H "Authorization: Bearer $ADMIN_TOKEN")
check_code "$ADMIN_DETAIL" "200" "管理员查看文章详情"
ARTICLE_STATUS=$(echo "$ADMIN_DETAIL" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('status',''))" 2>/dev/null)
if [ "$ARTICLE_STATUS" = "DRAFT" ]; then
    pass "文章状态为 DRAFT（草稿）"
else
    fail "文章状态应为 DRAFT" "got $ARTICLE_STATUS"
fi

# ---- 6. 编辑文章 ----
echo ""
echo "--- 6. 编辑文章 ---"
EDIT_ARTICLE=$(curl -s -X PUT "$BASE_URL/api/admin/articles/$ARTICLE_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{"title":"验证测试文章(已编辑)","summary":"编辑后的摘要","coverUrl":"","contentMarkdown":"# 验证测试（已编辑）\n\n内容已更新。","category":"测试分类","tags":"[\"测试\",\"验证\",\"已编辑\"]","sortOrder":1}')
check_code "$EDIT_ARTICLE" "200" "编辑文章"

# ---- 7. 草稿文章前台不可见 ----
echo ""
echo "--- 7. 草稿文章前台不可见 ---"
PUBLIC_LIST=$(curl -s "$BASE_URL/api/articles")
check_code "$PUBLIC_LIST" "200" "前台文章列表可访问"
ARTICLE_IN_LIST=$(echo "$PUBLIC_LIST" | python3 -c "
import sys,json
data=json.load(sys.stdin).get('data',[])
found=any(a.get('id')==$ARTICLE_ID for a in data)
print('yes' if found else 'no')
" 2>/dev/null)
if [ "$ARTICLE_IN_LIST" = "no" ]; then
    pass "草稿文章不在前台列表（正确）"
else
    fail "草稿文章不应出现在前台列表" ""
fi

# ---- 8. 发布文章 ----
echo ""
echo "--- 8. 发布文章 ---"
PUBLISH=$(curl -s -X PUT "$BASE_URL/api/admin/articles/$ARTICLE_ID/publish" \
    -H "Authorization: Bearer $ADMIN_TOKEN")
check_code "$PUBLISH" "200" "发布文章"

# ---- 9. 前台文章列表可见已发布文章 ----
echo ""
echo "--- 9. 前台文章列表可见已发布文章 ---"
PUBLIC_LIST2=$(curl -s "$BASE_URL/api/articles")
check_code "$PUBLIC_LIST2" "200" "前台文章列表"
ARTICLE_IN_LIST2=$(echo "$PUBLIC_LIST2" | python3 -c "
import sys,json
data=json.load(sys.stdin).get('data',[])
found=any(a.get('id')==$ARTICLE_ID for a in data)
print('yes' if found else 'no')
" 2>/dev/null)
if [ "$ARTICLE_IN_LIST2" = "yes" ]; then
    pass "已发布文章出现在前台列表"
else
    fail "已发布文章应在前台列表" ""
fi

# ---- 10. 前台文章详情可访问 ----
echo ""
echo "--- 10. 前台文章详情 ---"
PUBLIC_DETAIL=$(curl -s "$BASE_URL/api/articles/$ARTICLE_ID")
check_code "$PUBLIC_DETAIL" "200" "前台查看文章详情"

# ---- 11. Markdown 内容验证 ----
echo ""
echo "--- 11. Markdown 内容验证 ---"
ARTICLE_CONTENT=$(echo "$PUBLIC_DETAIL" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('contentMarkdown',''))" 2>/dev/null)
if echo "$ARTICLE_CONTENT" | grep -q "验证测试" ; then
    pass "Markdown 内容包含原标题"
else
    fail "Markdown 内容异常" "content=$ARTICLE_CONTENT"
fi

# ---- 12. 下架文章 ----
echo ""
echo "--- 12. 下架文章 ---"
OFFLINE=$(curl -s -X PUT "$BASE_URL/api/admin/articles/$ARTICLE_ID/offline" \
    -H "Authorization: Bearer $ADMIN_TOKEN")
check_code "$OFFLINE" "200" "下架文章"

# ---- 13. 下架后前台列表不可见 ----
echo ""
echo "--- 13. 下架后前台列表不可见 ---"
PUBLIC_LIST3=$(curl -s "$BASE_URL/api/articles")
ARTICLE_IN_LIST3=$(echo "$PUBLIC_LIST3" | python3 -c "
import sys,json
data=json.load(sys.stdin).get('data',[])
found=any(a.get('id')==$ARTICLE_ID for a in data)
print('yes' if found else 'no')
" 2>/dev/null)
if [ "$ARTICLE_IN_LIST3" = "no" ]; then
    pass "下架文章不在前台列表（正确）"
else
    fail "下架文章不应出现在前台列表" ""
fi

# ---- 14. 前台直接访问下架文章应失败 ----
echo ""
echo "--- 14. 前台直接访问下架文章 ---"
OFFLINE_ACCESS=$(curl -s "$BASE_URL/api/articles/$ARTICLE_ID")
check_code_ne "$OFFLINE_ACCESS" "200" "下架文章前台不可访问"

# ---- 15. 删除文章 ----
echo ""
echo "--- 15. 删除文章 ---"
DELETE_ARTICLE=$(curl -s -X DELETE "$BASE_URL/api/admin/articles/$ARTICLE_ID" \
    -H "Authorization: Bearer $ADMIN_TOKEN")
check_code "$DELETE_ARTICLE" "200" "删除文章"

# ---- 16. 删除后前台不可见 ----
echo ""
echo "--- 16. 删除后前台不可见 ---"
PUBLIC_LIST4=$(curl -s "$BASE_URL/api/articles")
ARTICLE_IN_LIST4=$(echo "$PUBLIC_LIST4" | python3 -c "
import sys,json
data=json.load(sys.stdin).get('data',[])
found=any(a.get('id')==$ARTICLE_ID for a in data)
print('yes' if found else 'no')
" 2>/dev/null)
if [ "$ARTICLE_IN_LIST4" = "no" ]; then
    pass "删除文章不在前台列表（正确）"
else
    fail "删除文章不应出现在前台列表" ""
fi

# ---- 17. 普通用户不能访问后台文章接口 ----
echo ""
echo "--- 17. 权限隔离验证 ---"

# 注册普通用户
TEST_USER="verify_article_$(date +%s)"
TEST_PASS="Verify@123"
REGISTER=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\",\"confirmPassword\":\"$TEST_PASS\",\"realName\":\"文章测试用户\",\"phone\":\"139$(printf '%08d' $((RANDOM % 100000000)))\",\"grade\":\"2024\",\"major\":\"计算机科学与技术\",\"className\":\"计科2401\"}")
# ignore register result, try normal login anyway
USER_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}")
USER_TOKEN=$(extract_token "$USER_LOGIN")

if [ -z "$USER_TOKEN" ]; then
    fail "无法获取普通用户 Token（可能注册已存在）" ""
else
    # 17a. 普通用户访问后台文章列表
    USER_ACCESS_ADMIN=$(curl -s "$BASE_URL/api/admin/articles" -H "Authorization: Bearer $USER_TOKEN")
    ADMIN_CODE=$(echo "$USER_ACCESS_ADMIN" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',''))" 2>/dev/null)
    if [ "$ADMIN_CODE" != "200" ]; then
        pass "普通用户无法访问后台文章接口（code=$ADMIN_CODE）"
    else
        fail "普通用户不应能访问后台文章接口" ""
    fi

    # 17b. 游客可访问前台文章列表
    GUEST_LIST=$(curl -s "$BASE_URL/api/articles")
    check_code "$GUEST_LIST" "200" "游客可访问前台文章列表"
fi

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
    echo "✅ 文章闭环验证全部通过！"
    exit 0
fi
