<template>
  <div class="site-config-page admin-page">
    <div class="page-header">
      <div>
        <span class="admin-page-kicker">Site Config</span>
        <h2 class="page-title">站点配置</h2>
        <p class="admin-page-copy">管理网站全局配置项，包含名称、标语、联系方式、页脚声明等。</p>
      </div>
    </div>

    <el-card shadow="never">
      <el-table
        :data="list"
        empty-text="暂无配置"
        v-loading="loading"
        @row-click="showEdit"
      >
        <el-table-column prop="configKey" label="配置键" min-width="180" show-overflow-tooltip />
        <el-table-column prop="configValue" label="配置值" min-width="240" show-overflow-tooltip />
        <el-table-column prop="groupName" label="分组" width="120">
          <template #default="{ row }">
            {{ row.groupName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click.stop="showEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="编辑配置"
      width="560px"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="配置键">
          <el-input :model-value="form.configKey" disabled />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input
            v-if="form.configType !== 'TEXTAREA'"
            v-model="form.configValue"
            placeholder="请输入配置值"
          />
          <el-input
            v-else
            v-model="form.configValue"
            type="textarea"
            :rows="4"
            placeholder="请输入配置值"
          />
        </el-form-item>
        <el-form-item label="分组">
          <el-input v-model="form.groupName" placeholder="请输入分组名称" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.remark" placeholder="请输入说明" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSiteConfigs, updateSiteConfig, type SiteConfigItem, type SiteConfigUpdateParams } from '@/api/siteConfig'

const list = ref<SiteConfigItem[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingKey = ref('')

const form = reactive<SiteConfigUpdateParams & { configType?: string }>({
  configKey: '',
  configValue: '',
  groupName: '',
  remark: '',
  configType: '',
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getSiteConfigs()
    if (res.code === 200) list.value = res.data || []
  } catch {
    ElMessage.error('加载配置失败')
  } finally {
    loading.value = false
  }
}

function showEdit(row: SiteConfigItem) {
  editingKey.value = row.configKey
  form.configKey = row.configKey
  form.configValue = row.configValue
  form.configType = row.configType
  form.groupName = row.groupName || ''
  form.remark = row.remark || ''
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    const res = await updateSiteConfig(editingKey.value, {
      configKey: form.configKey,
      configValue: form.configValue,
      groupName: form.groupName,
      remark: form.remark,
    })
    if (res.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      await fetchList()
    }
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.site-config-page {
  padding: 0;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-title {
  font-size: 26px;
  font-weight: 780;
  color: var(--admin-text-strong);
  margin: 0;
}
</style>
