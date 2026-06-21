#!/usr/bin/env bash
# =============================================
# NYNU Code Lab — 项目成果闭环验证脚本
# =============================================
# 用途：本地开发环境验证项目成果完整流程
# 默认访问 http://localhost
# 不要依赖线上环境，不要写真实密钥
# =============================================

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost}"
PASS=0
FAIL=0

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

pass() { PASS=$((PASS + 1)); echo -e "${GREEN}[PASS]${NC} $1"; }
fail() { FAIL=$((FAIL + 1)); echo -e "${RED}[FAIL]${NC} $1"; echo "       $2"; }
info() { echo -e "${YELLOW}[INFO]${NC} $1"; }

extract_token() {
    echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('token',''))" 2>/dev/null
}

extract_id() {
    echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin).get('data',{}).get('id',''))" 2>/dev/null
}

check_code() {
    echo "$1" | python3 -c "import sys,json; code=json.load(sys.stdin).get('code',-1); sys.exit(0 if code==200 else 1)" 2>/dev/null
}

get_code() {
    echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',''))" 2>/dev/null
}

# ---------- 1. 管理员登录 ----------
info "1. 管理员登录"
ADMIN_RESP=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')
ADMIN_TOKEN=$(extract_token "$ADMIN_RESP")
if [ -z "$ADMIN_TOKEN" ]; then
  fail "管理员登录" "响应: $ADMIN_RESP"
  exit 1
fi
pass "管理员登录"

# ---------- 2. 创建草稿项目 ----------
info "2. 创建草稿项目"
CREATE_RESP=$(curl -s -X POST "${BASE_URL}/api/admin/projects" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "title":"测试项目-验证脚本",
    "summary":"验证项目成果闭环的测试项目",
    "projectType":"Web",
    "techStack":"Java / Spring Boot / Vue",
    "leaderName":"测试负责人",
    "descriptionMarkdown":"# 项目介绍\n\n**测试项目**。\n\n## 功能\n\n- 功能A\n- 功能B\n\n```java\nSystem.out.println(\"Hello\");\n```"
  }')
PROJECT_ID=$(extract_id "$CREATE_RESP")
if [ -z "$PROJECT_ID" ]; then
  fail "创建草稿项目" "响应: $CREATE_RESP"
  exit 1
fi
pass "创建草稿项目，ID: $PROJECT_ID"

# ---------- 3. 查看后台项目列表 ----------
info "3. 查看后台项目列表"
LIST_RESP=$(curl -s "${BASE_URL}/api/admin/projects" -H "Authorization: Bearer ${ADMIN_TOKEN}")
if check_code "$LIST_RESP"; then
  pass "后台项目列表"
else
  fail "后台项目列表" "响应: $LIST_RESP"
fi

# ---------- 4. 查看后台项目详情 ----------
info "4. 查看后台项目详情"
DETAIL_RESP=$(curl -s "${BASE_URL}/api/admin/projects/${PROJECT_ID}" -H "Authorization: Bearer ${ADMIN_TOKEN}")
if echo "$DETAIL_RESP" | grep -q '"status":"DRAFT"'; then
  pass "后台项目详情（草稿状态）"
else
  fail "后台项目详情" "响应: $DETAIL_RESP"
fi

# ---------- 5. 编辑项目 ----------
info "5. 编辑项目"
EDIT_RESP=$(curl -s -X PUT "${BASE_URL}/api/admin/projects/${PROJECT_ID}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "title":"测试项目-已编辑",
    "summary":"更新后的简介",
    "projectType":"AI",
    "techStack":"Python / PyTorch",
    "leaderName":"更新后的负责人",
    "descriptionMarkdown":"# 已编辑\n\n项目已更新。",
    "featured":1,
    "sortOrder":100
  }')
if check_code "$EDIT_RESP"; then
  pass "编辑项目"
else
  fail "编辑项目" "响应: $EDIT_RESP"
fi

# ---------- 6. 草稿前台不可见 ----------
info "6. 草稿项目前台不可见"
PUB_LIST=$(curl -s "${BASE_URL}/api/projects")
if echo "$PUB_LIST" | grep -q "\"id\":${PROJECT_ID}"; then
  fail "草稿前台不可见" "草稿项目不应在前台列表出现"
else
  pass "草稿前台不可见"
fi

# ---------- 7. 发布项目 ----------
info "7. 发布项目"
PUBLISH_RESP=$(curl -s -X PUT "${BASE_URL}/api/admin/projects/${PROJECT_ID}/publish" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")
if echo "$PUBLISH_RESP" | grep -q '"status":"PUBLISHED"'; then
  pass "发布项目"
else
  fail "发布项目" "响应: $PUBLISH_RESP"
fi

# ---------- 8. 前台列表可见 ----------
info "8. 前台项目列表可见"
PUB_LIST2=$(curl -s "${BASE_URL}/api/projects")
if echo "$PUB_LIST2" | grep -q "\"id\":${PROJECT_ID}"; then
  pass "前台列表可见"
else
  fail "前台列表可见" "响应: $PUB_LIST2"
fi

# ---------- 9. 前台精选项目可见 ----------
info "9. 前台精选项目可见"
FEATURED_RESP=$(curl -s "${BASE_URL}/api/projects/featured")
if echo "$FEATURED_RESP" | grep -q "\"id\":${PROJECT_ID}"; then
  pass "精选项目可见"
else
  fail "精选项目可见" "响应: $FEATURED_RESP"
fi

# ---------- 10. 前台项目详情 ----------
info "10. 前台项目详情"
PUB_DETAIL=$(curl -s "${BASE_URL}/api/projects/${PROJECT_ID}")
if check_code "$PUB_DETAIL"; then
  pass "前台项目详情"
else
  fail "前台项目详情" "响应: $PUB_DETAIL"
fi

# ---------- 11. Markdown 内容返回 ----------
info "11. Markdown 内容返回"
if echo "$PUB_DETAIL" | grep -q 'descriptionMarkdown'; then
  pass "Markdown 内容返回"
else
  fail "Markdown 内容返回" "响应无 descriptionMarkdown"
fi

# ---------- 12. 下架项目 ----------
info "12. 下架项目"
OFFLINE_RESP=$(curl -s -X PUT "${BASE_URL}/api/admin/projects/${PROJECT_ID}/offline" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")
if echo "$OFFLINE_RESP" | grep -q '"status":"OFFLINE"'; then
  pass "下架项目"
else
  fail "下架项目" "响应: $OFFLINE_RESP"
fi

# ---------- 13. 下架后前台列表不可见 ----------
info "13. 下架后前台列表不可见"
PUB_LIST3=$(curl -s "${BASE_URL}/api/projects")
if echo "$PUB_LIST3" | grep -q "\"id\":${PROJECT_ID}"; then
  fail "下架后前台不可见" "下架项目不应在前台列表出现"
else
  pass "下架后前台列表不可见"
fi

# ---------- 14. 前台访问下架项目详情应失败 ----------
info "14. 前台访问下架项目详情"
OFFLINE_DETAIL=$(curl -s "${BASE_URL}/api/projects/${PROJECT_ID}")
if echo "$OFFLINE_DETAIL" | grep -q '"不存在\|未发布\|ERROR\|code":500'; then
  pass "下架项目前台不可访问"
else
  fail "下架项目前台不可访问" "响应: $OFFLINE_DETAIL"
fi

# ---------- 15. 删除项目 ----------
info "15. 删除项目"
DELETE_RESP=$(curl -s -X DELETE "${BASE_URL}/api/admin/projects/${PROJECT_ID}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")
if check_code "$DELETE_RESP"; then
  pass "删除项目"
else
  fail "删除项目" "响应: $DELETE_RESP"
fi

# ---------- 16. 删除后前台不可见 ----------
info "16. 删除后前台不可见"
PUB_LIST4=$(curl -s "${BASE_URL}/api/projects")
if echo "$PUB_LIST4" | grep -q "\"id\":${PROJECT_ID}"; then
  fail "删除后前台不可见" "已删除项目不应在前台出现"
else
  pass "删除后前台不可见"
fi

# ---------- 17. 注册并登录普通用户 ----------
info "17. 注册普通用户"
USERNAME="testproj_$(date +%s)"
USER_RESP=$(curl -s -X POST "${BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\":\"${USERNAME}\",
    \"password\":\"test123456\",
    \"realName\":\"测试\",
    \"phone\":\"139$(date +%s | tail -c 9)\",
    \"grade\":\"大二\",
    \"major\":\"计科\",
    \"className\":\"计科2201\",
    \"confirmPassword\":\"test123456\"
  }")
if check_code "$USER_RESP"; then
  pass "注册普通用户"
else
  fail "注册普通用户" "响应: $USER_RESP"
fi

info "18. 普通用户登录"
USER_LOGIN=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"${USERNAME}\",\"password\":\"test123456\"}")
USER_TOKEN=$(extract_token "$USER_LOGIN")
if [ -z "$USER_TOKEN" ]; then
  fail "普通用户登录" "响应: $USER_LOGIN"
else
  pass "普通用户登录"
fi

# ---------- 19. 普通用户访问后台项目接口 ----------
info "19. 普通用户访问后台项目列表（应被拒绝）"
USER_LIST=$(curl -s "${BASE_URL}/api/admin/projects" -H "Authorization: Bearer ${USER_TOKEN}")
USER_LIST_CODE=$(get_code "$USER_LIST")
if [ "$USER_LIST_CODE" = "401" ] || [ "$USER_LIST_CODE" = "403" ]; then
  pass "普通用户被拒绝访问后台项目列表（code=${USER_LIST_CODE}）"
else
  fail "普通用户权限隔离" "应返回 401/403，实际响应: $USER_LIST"
fi

# ---------- 20. 游客访问已发布项目列表 ----------
info "20. 游客访问已发布项目列表"
GUEST_LIST=$(curl -s "${BASE_URL}/api/projects")
if check_code "$GUEST_LIST"; then
  pass "游客可访问项目列表"
else
  fail "游客访问项目列表" "响应: $GUEST_LIST"
fi

# ---------- 21. 创建公开项目并验证 ----------
info "21. 创建并发布公开项目"
NEW_RESP=$(curl -s -X POST "${BASE_URL}/api/admin/projects" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "title":"公开展示项目",
    "summary":"用于前台展示验证",
    "projectType":"Web",
    "techStack":"Vue / Spring Boot",
    "leaderName":"张三",
    "descriptionMarkdown":"# 公开项目\n\n前台可见。",
    "featured":1
  }')
NEW_ID=$(extract_id "$NEW_RESP")
if [ -z "$NEW_ID" ]; then
  fail "创建公开项目" "响应: $NEW_RESP"
else
  # 发布
  curl -s -X PUT "${BASE_URL}/api/admin/projects/${NEW_ID}/publish" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" > /dev/null

  GUEST_DETAIL=$(curl -s "${BASE_URL}/api/projects/${NEW_ID}")
  if check_code "$GUEST_DETAIL"; then
    pass "游客可访问项目详情"
  else
    fail "游客访问项目详情" "响应: $GUEST_DETAIL"
  fi
fi

# ---------- 总结 ----------
echo ""
echo "========================================"
echo "  项目成果验证: 通过 ${PASS} / 失败 ${FAIL}"
echo "========================================"

if [ "$FAIL" -gt 0 ]; then
  echo "存在失败项，请检查。"
  exit 1
else
  echo "项目成果闭环验证全部通过 ✅"
  exit 0
fi
