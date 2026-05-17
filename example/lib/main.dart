import 'package:flutter/material.dart';
import 'package:notification_badge/notification_badge.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Notification Badge Example',
      theme: ThemeData(colorSchemeSeed: Colors.indigo, useMaterial3: true),
      home: const BadgeDemoPage(),
    );
  }
}

class BadgeDemoPage extends StatefulWidget {
  const BadgeDemoPage({super.key});

  @override
  State<BadgeDemoPage> createState() => _BadgeDemoPageState();
}

class _BadgeDemoPageState extends State<BadgeDemoPage> {
  final _badge = NotificationBadgeApi();
  final _setCountController = TextEditingController();
  final _iconController = TextEditingController(text: 'ic_notification');
  final _titleController = TextEditingController(text: 'Notification Badge Counter Example');
  final _messageController = TextEditingController(text: 'You May Have Notifications');
  bool _fallbackToUniversal = true;

  int _badgeCount = 0;
  bool _hasPermission = false;
  bool _isSupported = false;
  String _manufacturer = '';
  String _status = '';

  @override
  void initState() {
    super.initState();
    _init();
  }

  @override
  void dispose() {
    _setCountController.dispose();
    _iconController.dispose();
    _titleController.dispose();
    _messageController.dispose();
    super.dispose();
  }

  Future<void> _init() async {
    final supported = await _badge.isSupported();
    final manufacturer = await _badge.getDeviceManufacturer();
    final count = await _badge.getBadgeCount();
    final permission = await _badge.checkPermissions();
    setState(() {
      _isSupported = supported;
      _manufacturer = manufacturer;
      _badgeCount = count;
      _hasPermission = permission;
    });
  }

  Future<void> _refreshCount() async {
    final count = await _badge.getBadgeCount();
    setState(() => _badgeCount = count);
  }

  Future<void> _requestPermissions() async {
    final granted = await _badge.requestPermissions();
    setState(() {
      _hasPermission = granted;
      _status = granted ? 'Permission granted' : 'Permission denied';
    });
  }

  Future<void> _setCount() async {
    final value = int.tryParse(_setCountController.text);
    if (value == null) {
      setState(() => _status = 'Enter a valid number');
      return;
    }
    final success = await _badge.setCount(value);
    if (success) await _refreshCount();
    setState(
      () => _status = success ? 'Badge set to $value' : 'Failed to set badge',
    );
  }

  Future<void> _increment() async {
    final success = await _badge.incrementCount();
    if (success) await _refreshCount();
    setState(
      () => _status = success ? 'Badge incremented' : 'Failed to increment',
    );
  }

  Future<void> _decrement() async {
    final success = await _badge.decrementCount();
    if (success) await _refreshCount();
    setState(
      () => _status = success ? 'Badge decremented' : 'Failed to decrement',
    );
  }

  Future<void> _clear() async {
    final success = await _badge.clearBadge();
    if (success) await _refreshCount();
    setState(
      () => _status = success ? 'Badge cleared' : 'Failed to clear badge',
    );
  }

  Future<void> _applyNotificationConfig() async {
    final icon = _iconController.text.trim();
    if (icon.isEmpty) {
      setState(() => _status = 'Icon name cannot be empty');
      return;
    }
    final success = await _badge.setAndroidNotificationConfig(
      notificationIcon: icon,
      notificationTitle: _titleController.text.trim().isEmpty
          ? null
          : _titleController.text.trim(),
      notificationMessage: _messageController.text.trim().isEmpty
          ? null
          : _messageController.text.trim(),
      fallbackToUniversaLAndroidBadger: _fallbackToUniversal,
    );
    setState(
      () => _status =
          success ? 'Notification config applied' : 'Failed to apply config',
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(title: const Text('Notification Badge')),
      body: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          _InfoCard(
            children: [
              _InfoRow('Manufacturer', _manufacturer),
              _InfoRow('Supported', _isSupported ? 'Yes' : 'No'),
              _InfoRow(
                'Permission',
                _hasPermission ? 'Granted' : 'Not granted',
              ),
            ],
          ),
          const SizedBox(height: 24),
          Center(
            child: Column(
              children: [
                Text('Current badge count', style: theme.textTheme.labelLarge),
                const SizedBox(height: 8),
                Text(
                  '$_badgeCount',
                  style: theme.textTheme.displayMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: theme.colorScheme.primary,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),
          if (!_hasPermission)
            FilledButton.icon(
              onPressed: _requestPermissions,
              icon: const Icon(Icons.notifications),
              label: const Text('Request Permissions'),
            ),
          if (_hasPermission) ...[
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _decrement,
                    icon: const Icon(Icons.remove),
                    label: const Text('Decrement'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: FilledButton.icon(
                    onPressed: _increment,
                    icon: const Icon(Icons.add),
                    label: const Text('Increment'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _setCountController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Set count',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                FilledButton(onPressed: _setCount, child: const Text('Set')),
              ],
            ),
            const SizedBox(height: 12),
            OutlinedButton.icon(
              onPressed: _clear,
              icon: const Icon(Icons.clear),
              label: const Text('Clear Badge'),
              style: OutlinedButton.styleFrom(
                foregroundColor: theme.colorScheme.error,
              ),
            ),
          ],
          const SizedBox(height: 28),
          Text(
            'Notification Config (Android)',
            style: theme.textTheme.titleSmall?.copyWith(
              color: theme.colorScheme.onSurfaceVariant,
            ),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _iconController,
            decoration: const InputDecoration(
              labelText: 'Icon drawable name',
              hintText: 'ic_notification',
              border: OutlineInputBorder(),
              isDense: true,
            ),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _titleController,
            decoration: const InputDecoration(
              labelText: 'Notification title (optional)',
              border: OutlineInputBorder(),
              isDense: true,
            ),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _messageController,
            decoration: const InputDecoration(
              labelText: 'Notification message (optional)',
              border: OutlineInputBorder(),
              isDense: true,
            ),
          ),
          const SizedBox(height: 8),
          SwitchListTile(
            contentPadding: EdgeInsets.zero,
            title: const Text('Fallback to universal badge provider'),
            value: _fallbackToUniversal,
            onChanged: (v) => setState(() => _fallbackToUniversal = v),
          ),
          FilledButton.tonal(
            onPressed: _applyNotificationConfig,
            child: const Text('Apply Config'),
          ),
          if (_status.isNotEmpty) ...[
            const SizedBox(height: 20),
            Text(
              _status,
              textAlign: TextAlign.center,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.secondary,
              ),
            ),
          ],
        ],
      ),
    );
  }
}

class _InfoCard extends StatelessWidget {
  const _InfoCard({required this.children});
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: children,
        ),
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  const _InfoRow(this.label, this.value);
  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: theme.textTheme.bodyMedium),
          Text(
            value,
            style: theme.textTheme.bodyMedium?.copyWith(
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }
}
